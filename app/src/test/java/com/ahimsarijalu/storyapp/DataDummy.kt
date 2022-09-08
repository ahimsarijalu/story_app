package com.ahimsarijalu.storyapp

import androidx.paging.PagingData
import com.ahimsarijalu.storyapp.data.remote.response.*
import com.google.android.gms.maps.model.LatLng
import java.io.File

object DataDummy {

    fun generateDummyUser(): UserCredential {
        return UserCredential("Dummy", "dummy@dummy.com", "password")
    }

    fun generateDummyRegister(): AuthResponse {
        return AuthResponse(
            error = false,
            message = "Success"
        )
    }

    fun generateDummyLogin(): AuthResponse {
        return AuthResponse(
            LoginResult("John", "dummyUID", "dummyToken"),
            false,
            "Success"
        )
    }

    fun generateDummyStory(): StoryEntitiy {
        return StoryEntitiy(
            File.createTempFile("dummyFile", "dummyFile"),
            "description",
            "token",
            LatLng(0.0, 0.0)
        )
    }

    fun generateDummyStoryResponse(): StoryResponse {
        val storyItem = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "dummyID-$i",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                "John $i",
                "description $i",
                i.toDouble(),
                i.toDouble()
            )
            storyItem.add(story)
        }
        return StoryResponse(
            false,
            "Success",
            storyItem
        )
    }

    fun generatePagingData(): PagingData<ListStoryItem> =
        PagingData.from(generateDummyStoryResponse().listStory)

    fun generateDummyUpload(): FileUploadResponse {
        return FileUploadResponse(false, "Success")
    }

    fun generateDummyUserDatastore(): LoginResult {
        return LoginResult("Dummy", "userId", "token")
    }
}