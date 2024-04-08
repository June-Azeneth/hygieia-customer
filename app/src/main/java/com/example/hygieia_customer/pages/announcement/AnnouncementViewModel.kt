package com.example.hygieia_customer.pages.announcement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Announcement
import com.example.hygieia_customer.repository.AnnouncementRepo
import kotlinx.coroutines.launch

class AnnouncementViewModel : ViewModel() {
    private val announcementRepo: AnnouncementRepo = AnnouncementRepo()
    private val _announcementDetails = MutableLiveData<List<Announcement>>()
    val announcementDetails: LiveData<List<Announcement>> get() = _announcementDetails

    private val _singleAnnouncement = MutableLiveData<Announcement?>()
    val singleAnnouncement: MutableLiveData<Announcement?> get() = _singleAnnouncement

    fun getAllAnnouncements() {
        viewModelScope.launch {
            announcementRepo.fetchAllAnnouncements { announcements ->
                _announcementDetails.value = announcements
            }
        }
    }

    fun clearAnnouncement() {
        _singleAnnouncement.postValue(null)
    }

    fun fetchAnnouncement(id: String) {
        viewModelScope.launch {
            announcementRepo.fetchAnnouncement(id) { announcement ->
                _singleAnnouncement.value = announcement
            }
        }
    }
}
