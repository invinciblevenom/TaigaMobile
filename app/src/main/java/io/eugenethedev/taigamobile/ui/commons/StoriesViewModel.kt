package io.eugenethedev.taigamobile.ui.commons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.domain.repositories.IStoriesRepository
import io.eugenethedev.taigamobile.ui.utils.MutableLiveResult
import io.eugenethedev.taigamobile.ui.utils.Result
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

abstract class StoriesViewModel : ViewModel() {

    @Inject lateinit var storiesRepository: IStoriesRepository

    val statuses = MutableLiveResult<List<Status>>()
    val stories = MutableLiveResult<List<Story>>()

    val loadingStatusIds = MutableLiveData(emptyList<Long>())
    val visibleStatusIds = MutableLiveData(emptyList<Long>())

    private val statusesStates = mutableMapOf<Status, StatusState>()

    private class StatusState {
        var currentPage = 0
        var maxPage = Int.MAX_VALUE
    }

    protected var sprintId: Long? = null

    protected suspend fun loadStatuses() {
        statuses.value = Result(ResultStatus.LOADING)
        stories.value = Result(ResultStatus.SUCCESS)

        try {
            statuses.value = Result(
                resultStatus = ResultStatus.SUCCESS,
                storiesRepository.getStatuses(sprintId).onEach {
                    statusesStates[it] = StatusState()
                    loadStories(it)
                }
            )

        } catch (e: Exception) {
            Timber.w(e)
            statuses.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
        }
    }

    fun loadStories(status: Status) = viewModelScope.launch {
        statusesStates[status]?.apply {
            if (currentPage == maxPage) return@launch

            loadingStatusIds.value = loadingStatusIds.value.orEmpty() + status.id

            try {
                storiesRepository.getStories(status.id, ++currentPage, sprintId)
                    .also { stories.value = Result(ResultStatus.SUCCESS, stories.value?.data.orEmpty() + it) }
                    .takeIf { it.isEmpty() }
                    ?.run { maxPage = currentPage /* reached maximum page */ }
            } catch (e: Exception) {
                Timber.w(e)
                statuses.value = Result(ResultStatus.ERROR, message = R.string.common_error_message)
            }

            loadingStatusIds.value = loadingStatusIds.value.orEmpty() - status.id
        }
    }

    fun statusClick(statusId: Long) {
        visibleStatusIds.value = if (statusId in visibleStatusIds.value.orEmpty()) {
            visibleStatusIds.value.orEmpty() - statusId
        } else {
            visibleStatusIds.value.orEmpty() + statusId
        }
    }

    open fun reset() {
        sprintId = null
        statuses.value = null
        stories.value = null
        statusesStates.clear()
        loadingStatusIds.value = emptyList()
        visibleStatusIds.value = emptyList()
    }
}