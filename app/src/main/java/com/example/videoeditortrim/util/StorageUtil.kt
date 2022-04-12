package com.example.videoeditortrim.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.File
import java.util.*

/**
 * Helper class for getting all storages directories in an Android device
 * [Solution of this problem](https://stackoverflow.com/a/40582634/3940133)
 * Consider to use
 * [StorageAccessFramework(SAF)](https://developer.android.com/guide/topics/providers/document-provider)>
 * if your min SDK version is 19 and your requirement is just for browse and open documents, images, and other files
 *
 * @author Dmitriy Lozenko, HendraWD
 */
object StorageUtil {
    // Primary physical SD-CARD (not emulated)
    private val EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE")

    // All Secondary SD-CARDs (all exclude primary) separated by File.pathSeparator, i.e: ":", ";"
    private val SECONDARY_STORAGES = System.getenv("SECONDARY_STORAGE")

    // Primary emulated SD-CARD
    private val EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET")

    // PhysicalPaths based on phone model
    @SuppressLint("SdCardPath")
    private val KNOWN_PHYSICAL_PATHS = arrayOf(
        "/storage/sdcard0",
        "/storage/sdcard1",  //Motorola Xoom
        "/storage/extsdcard",  //Samsung SGS3
        "/storage/sdcard0/external_sdcard",  //User request
        "/mnt/extsdcard",
        "/mnt/sdcard/external_sd",  //Samsung galaxy family
        "/mnt/sdcard/ext_sd",
        "/mnt/external_sd",
        "/mnt/media_rw/sdcard1",  //4.4.2 on CyanogenMod S3
        "/removable/microsd",  //Asus transformer prime
        "/mnt/emmc",
        "/storage/external_SD",  //LG
        "/storage/ext_sd",  //HTC One Max
        "/storage/removable/sdcard1",  //Sony Xperia Z1
        "/data/sdext",
        "/data/sdext2",
        "/data/sdext3",
        "/data/sdext4",
        "/sdcard1",  //Sony Xperia Z
        "/sdcard2",  //HTC One M8s
        "/storage/microsd" //ASUS ZenFone 2
    )

    /**
     * Returns all available storages in the system (include emulated)
     *
     *
     * Warning: Hack! Based on Android source code of version 4.3 (API 18)
     * Because there is no standard way to get it.
     *
     * @return paths to all available storages in the system (include emulated)
     */
    fun getStorageDirectories(context: Context): Array<String> {
        // Final set of paths
        val availableDirectoriesSet: HashSet<String> = HashSet()
        if (!TextUtils.isEmpty(EMULATED_STORAGE_TARGET)) {
            // Device has an emulated storage
            availableDirectoriesSet.add(emulatedStorageTarget)
        } else {
            // Device doesn't have an emulated storage
            availableDirectoriesSet.addAll(getExternalStorage(context))
        }

        // Add all secondary storages
        Collections.addAll(availableDirectoriesSet.toMutableSet(), *allSecondaryStorages)
        val storagesArray = arrayOfNulls<String>(availableDirectoriesSet.size)
        return availableDirectoriesSet.toArray(storagesArray)
    }

    private fun getExternalStorage(context: Context): Set<String> {
        val availableDirectoriesSet: MutableSet<String> = HashSet()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Solution of empty raw emulated storage for android version >= marshmallow
            // because the EXTERNAL_STORAGE become something like: "/Storage/A5F9-15F4",
            // so we can't access it directly
            val files = getExternalFilesDirs(context, null)
            for (file in files) {
                if (file != null) {
                    val applicationSpecificAbsolutePath = file.absolutePath
                    val rootPath = applicationSpecificAbsolutePath.substring(
                        0,
                        applicationSpecificAbsolutePath.indexOf("Android/data")
                    )
                    availableDirectoriesSet.add(rootPath)
                }
            }
        } else {
            if (TextUtils.isEmpty(EXTERNAL_STORAGE)) {
                availableDirectoriesSet.addAll(availablePhysicalPaths)
            } else {
                // Device has physical external storage; use plain paths.
                availableDirectoriesSet.add(EXTERNAL_STORAGE)
            }
        }
        return availableDirectoriesSet
    }

    // External storage paths should have storageId in the last segment
    // i.e: "/storage/emulated/storageId" where storageId is 0, 1, 2, ...
    private val emulatedStorageTarget: String
        get() {
            var rawStorageId = ""
            // External storage paths should have storageId in the last segment
            // i.e: "/storage/emulated/storageId" where storageId is 0, 1, 2, ...
            val path = Environment.getExternalStorageDirectory().absolutePath
            val folders = path.split(File.separator).toTypedArray()
            val lastSegment = folders[folders.size - 1]
            if (!TextUtils.isEmpty(lastSegment) && TextUtils.isDigitsOnly(lastSegment)) {
                rawStorageId = lastSegment
            }
            return if (TextUtils.isEmpty(rawStorageId)) {
                EMULATED_STORAGE_TARGET
            } else {
                EMULATED_STORAGE_TARGET + File.separator + rawStorageId
            }
        }

    // All Secondary SD-CARDs split into array
    private val allSecondaryStorages: Array<String?>
        get() = if (!TextUtils.isEmpty(SECONDARY_STORAGES)) {
            // All Secondary SD-CARDs split into array
            SECONDARY_STORAGES.split(File.pathSeparator).toTypedArray()
        } else arrayOfNulls(0)

    /**
     * Filter available physical paths from known physical paths
     *
     * @return List of available physical paths from current device
     */
    private val availablePhysicalPaths: List<String>
        get() {
            val availablePhysicalPaths: MutableList<String> = ArrayList()
            for (physicalPath in KNOWN_PHYSICAL_PATHS) {
                val file = File(physicalPath)
                if (file.exists()) {
                    availablePhysicalPaths.add(physicalPath)
                }
            }
            return availablePhysicalPaths
        }

    /**
     * Returns absolute paths to application-specific directories on all
     * external storage devices where the application can place persistent files
     * it owns. These files are internal to the application, and not typically
     * visible to the user as media.
     *
     *
     * This is like [Context.getFilesDir] in that these files will be
     * deleted when the application is uninstalled, however there are some
     * important differences:
     *
     *  * External files are not always available: they will disappear if the
     * user mounts the external storage on a computer or removes it.
     *  * There is no security enforced with these files.
     *
     *
     *
     * External storage devices returned here are considered a permanent part of
     * the device, including both emulated external storage and physical media
     * slots, such as SD cards in a battery compartment. The returned paths do
     * not include transient devices, such as USB flash drives.
     *
     *
     * An application may store data on any or all of the returned devices. For
     * example, an app may choose to store large files on the device with the
     * most available space, as measured by [android.os.StatFs].
     *
     *
     * Starting in [Build.VERSION_CODES.KITKAT], no permissions
     * are required to write to the returned paths; they're always accessible to
     * the calling app. Before then,
     * [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] is required to
     * write. Write access outside of these paths on secondary external storage
     * devices is not available. To request external storage access in a
     * backwards compatible way, consider using `android:maxSdkVersion`
     * like this:
     *
     * <pre class="prettyprint">&lt;uses-permission
     * android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * android:maxSdkVersion="18" /&gt;</pre>
     *
     *
     * The first path returned is the same as
     * [Context.getExternalFilesDir]. Returned paths may be
     * `null` if a storage device is unavailable.
     *
     * @see Context.getExternalFilesDir
     */
    private fun getExternalFilesDirs(context: Context, type: String?): Array<File?> {
        return context.getExternalFilesDirs(type)

    }
}