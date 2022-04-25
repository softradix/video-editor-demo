package com.example.videoeditortrim.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.videoeditortrim.R
import com.example.videoeditortrim.util.Constants
import ly.img.android.pesdk.VideoEditorSettingsList
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic
import ly.img.android.pesdk.assets.font.basic.FontPackBasic
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes
import ly.img.android.pesdk.backend.model.EditorSDKResult
import ly.img.android.pesdk.backend.model.constant.OutputMode
import ly.img.android.pesdk.backend.model.state.LoadSettings
import ly.img.android.pesdk.backend.model.state.VideoEditorSaveSettings
import ly.img.android.pesdk.ui.activity.VideoEditorBuilder
import ly.img.android.pesdk.ui.model.state.*
import ly.img.android.pesdk.ui.panels.item.PersonalStickerAddItem
import ly.img.android.serializer._3.IMGLYFileWriter
import java.io.File
import java.io.IOException

class KVideoEditorDemoActivity : AppCompatActivity() {
    companion object {
        const val VIDEO_EDITOR_SDK_RESULT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kvideo_editor_demo)

        val extraIntent: Intent = intent
        val path: Uri = Uri.parse(extraIntent.getStringExtra(Constants.VideoUri).toString())

        openEditor(path)

    }

    private fun openEditor(inputSource: Uri?) {
        val settingsList = createVESDKSettingsList()
            .configure<LoadSettings> {
                it.source = inputSource
            }

        VideoEditorBuilder(this)
            .setSettingsList(settingsList)
            .startActivityForResult(this, VIDEO_EDITOR_SDK_RESULT)

        settingsList.release()
    }

    private fun createVESDKSettingsList() = VideoEditorSettingsList(true)
        .configure<UiConfigFilter> { it.setFilterList(FilterPackBasic.getFilterPack()) }
        .configure<UiConfigText> { it.setFontList(FontPackBasic.getFontPack()) }
        .configure<UiConfigFrame> { it.setFrameList(FramePackBasic.getFramePack()) }
        .configure<UiConfigOverlay> { it.setOverlayList(OverlayPackBasic.getOverlayPack()) }
        .configure<UiConfigSticker> {
            it.setStickerLists(
                PersonalStickerAddItem(),
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
            )
        }.configure<VideoEditorSaveSettings> {
            // Set custom editor video export settings
            it.setOutputToGallery(Environment.DIRECTORY_DCIM)
            it.outputMode = OutputMode.EXPORT_IF_NECESSARY
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        intent ?: return
        if (resultCode == RESULT_OK && requestCode == VIDEO_EDITOR_SDK_RESULT) {
            // Editor has an result.
            val data = EditorSDKResult(intent)

            Log.i("VESDK", "Source video is located here ${data.sourceUri}")
            Log.i("VESDK", "Result video is located here ${data.resultUri}")

            // OPTIONAL: read the latest state to save it as a serialisation
            val lastState = data.settingsList
            try {
                IMGLYFileWriter(lastState).writeJson(
                    File(
                        getExternalFilesDir(null),
                        "serialisationReadyToReadWithPESDKFileReader.json"
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }

            lastState.release()

            if (data.resultUri != null) {
                val appIntent = Intent(applicationContext, FinalVideoActivity::class.java)
                appIntent.putExtra(Constants.VideoUri, data.resultUri.toString())
                startActivity(appIntent)
                finish()
            }

        } else if (resultCode == RESULT_CANCELED && requestCode == VIDEO_EDITOR_SDK_RESULT) {
            // Editor was canceled
            val data = EditorSDKResult(intent)
            val sourceURI = data.sourceUri
            // TODO: Do something with the source...
        }
    }
}