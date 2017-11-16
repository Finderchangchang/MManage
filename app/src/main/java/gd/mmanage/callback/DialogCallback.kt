package wai.gr.cla.callback

import android.app.Activity
import android.app.ProgressDialog
import android.view.Window
import android.widget.Toast

import com.lzy.okgo.request.BaseRequest
import okhttp3.Call
import okhttp3.Response

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：对于网络请求是否需要弹出进度对话框
 * 修订历史：
 * ================================================
 */
abstract class DialogCallback<T> : JsonCallback<T> {

    private var dialog: ProgressDialog? = null
    //var  kp_dialog:KProgressHUD?=null
    internal var isCloseEnd = true

    /**
     * @param activity
     * *
     * @param closeEnd
     */
    private fun initDialog(activity: Activity, closeEnd: Boolean) {
//        dialog = ProgressDialog(activity)
//        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog!!.setCanceledOnTouchOutside(false)
//        dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
////        dialog!!.setMessage("请求网络中...")
//        kp_dialog= KProgressHUD.create(activity)
//                .setStyle(com.kaopiz.kprogresshud.KProgressHUD.Style.SPIN_INDETERMINATE)
//                .setCancellable(true)
//                .show()
        isCloseEnd = closeEnd
    }

    constructor(activity: Activity, closeEnd: Boolean) : super() {
        initDialog(activity, closeEnd)
    }

    constructor(activity: Activity) : super() {
        initDialog(activity, true)
    }

    override fun onBefore(request: BaseRequest<*>?) {
        super.onBefore(request)
        //网络请求前显示对话框
        if (dialog != null && !dialog!!.isShowing) {
            dialog!!.show()
        }
//        if(kp_dialog!=null&&!kp_dialog!!.isShowing){
//            kp_dialog!!.show()
//        }
    }

    override fun onAfter(t: T?, e: Exception?) {
        super.onAfter(t, e)
        //网络请求结束后关闭对话框
        if (dialog != null && dialog!!.isShowing && isCloseEnd) {
            dialog!!.dismiss()
        }
//        if (kp_dialog != null && kp_dialog!!.isShowing && isCloseEnd) {
//            kp_dialog!!.dismiss()
//        }
    }

}
