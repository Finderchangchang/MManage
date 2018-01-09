package gd.mmanage.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.arialyy.frame.module.AbsModule
import com.jiangyy.easydialog.LoadingDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.LQRPhotoSelectUtils
import gd.mmanage.control.UserModule
import gd.mmanage.databinding.ActivityUpdateCompanyBinding
import gd.mmanage.method.ImgUtils
import gd.mmanage.method.Utils.compressImage
import gd.mmanage.method.uu
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EnterpriseModel
import gd.mmanage.model.FileModel
import gd.mmanage.model.NormalRequest
import kotlinx.android.synthetic.main.activity_update_company.*
import net.tsz.afinal.FinalDb
import org.json.JSONArray

class UpdateCompanyActivity : BaseActivity<ActivityUpdateCompanyBinding>(), AbsModule.OnCallback {
    override fun onSuccess(result: Int, success: Any?) {
        success as NormalRequest<JSONArray>
        if (success.code == 0) {
            setResult(77)
            toast("修改成功")
            finish()
        } else {
            toast(success.message)
        }
        dialog!!.dismiss()
    }

    override fun onError(result: Int, error: Any?) {
        toast("修改失败")
        dialog!!.dismiss()
    }

    var ve_array: Array<String?>? = null//车辆状态的集合
    var mLqrPhotoSelectUtils: LQRPhotoSelectUtils? = null
    var left_bitmap: Bitmap? = null
    var right_bitmap: Bitmap? = null
    var dialog: LoadingDialog.Builder? = null
    var model: EnterpriseModel = EnterpriseModel()
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        dialog = LoadingDialog.Builder(this).setTitle("正在加载...")//初始化dialog
        var model = intent.getSerializableExtra("model") as EnterpriseModel
        binding.model = model
        ll1.setOnClickListener {
            var s = FinalDb.create(this).findAllByWhere(CodeModel::class.java, " CodeName='Code_EnterpriseVehicleType'")
            ve_array = arrayOfNulls(s!!.size)
            for (id in 0 until ve_array!!.size) {
                var m = s[id]
                ve_array!![id] = m.Name
            }
            val builder = AlertDialog.Builder(this)
            builder.setTitle("请选择修车类型")
            builder.setItems(ve_array) { dialog, which ->
                model.EnterpriseVehicleType = s[which].ID
                model.EnterpriseVehicleTypeName = s[which].Name
                binding.model = model//刷新数据
            }
            builder.setNegativeButton("确定") { a, b -> }
            builder.create().show()
        }
        //工商营业执照拍照
        iv1_ll.setOnClickListener {
            mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                var bmp = uu.getimage(100, outputFile.absolutePath)
                left_bitmap = compressImage(uu.rotaingImageView(0, compressImage(bmp)))
                iv1.setImageBitmap(left_bitmap)

            }, false)
            mLqrPhotoSelectUtils!!.takePhoto()
        }
        //法人身份证拍照
        iv2_ll.setOnClickListener {
            mLqrPhotoSelectUtils = LQRPhotoSelectUtils(this, LQRPhotoSelectUtils.PhotoSelectListener { outputFile, outputUri ->
                var bmp = uu.getimage(100, outputFile.absolutePath)
                right_bitmap = compressImage(uu.rotaingImageView(0, compressImage(bmp)))
                iv2.setImageBitmap(right_bitmap)
            }, false)
            mLqrPhotoSelectUtils!!.takePhoto()
        }
        save_btn.setOnClickListener {
            builder.setTitle("提示")
            builder.setMessage("企业信息只可修改一次，请认真填写每项")
            builder.setPositiveButton("确定") { a, b ->
                if (left_bitmap == null || right_bitmap == null) {
                    toast("请将照片补充完整")
                } else {
                    dialog!!.show()
                    model = binding.model
                    model.files.add(FileModel(ImgUtils().Only_bitmapToBase64(left_bitmap), "工商营业执照", "A1", model.EnterpriseId))
                    model.files.add(FileModel(ImgUtils().Only_bitmapToBase64(right_bitmap), "法人身份证", "A2", model.EnterpriseId))
                    getModule(UserModule::class.java, this).update_company_details(binding.model)
                }
            }
            builder.setNeutralButton("取消") { a, b -> }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mLqrPhotoSelectUtils!!.attachToActivityForResult(requestCode, resultCode, data)
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_update_company
    }
}
