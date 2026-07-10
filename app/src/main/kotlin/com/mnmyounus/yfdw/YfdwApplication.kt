package com.mnmyounus.yfdw

import android.app.Application
import com.mnmyounus.yfdw.data.security.SecurityGate
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class YfdwApplication : Application() {

    @Inject lateinit var securityGate: SecurityGate

    override fun onCreate() {
        super.onCreate()
        // Armed from process start, before MainActivity draws a frame — no
        // window where the app is usable on an insecure network while in
        // PRIVACY_ANONYMITY mode. No-op in STANDARD_SECURE mode.
        securityGate.start()
    }
}
