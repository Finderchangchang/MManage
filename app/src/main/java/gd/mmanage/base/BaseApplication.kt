package gd.mmanage.base

import android.app.Application
import android.content.Context
import android.os.Handler

import com.arialyy.frame.core.AbsFrame
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created by Finder丶畅畅 on 2017/11/4 22:18
 * QQ群481606175
 */

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        AbsFrame.init(this)
        mHandler = Handler()
        CrashReport.initCrashReport(applicationContext, "2b022a71c0", false);
    }

    companion object {
        var context: Context? = null
        var mHandler: Handler? = null
    }
}
