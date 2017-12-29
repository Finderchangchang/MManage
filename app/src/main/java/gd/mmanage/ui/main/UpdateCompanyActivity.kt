package gd.mmanage.ui.main

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.config.LQRPhotoSelectUtils
import gd.mmanage.databinding.ActivityUpdateCompanyBinding
import gd.mmanage.method.Utils.compressImage
import gd.mmanage.method.uu
import gd.mmanage.model.CodeModel
import gd.mmanage.model.EnterpriseModel
import kotlinx.android.synthetic.main.activity_update_company.*
import net.tsz.afinal.FinalDb

class UpdateCompanyActivity : BaseActivity<ActivityUpdateCompanyBinding>() {
    var ve_array: Array<String?>? = null//车辆状态的集合
    var mLqrPhotoSelectUtils: LQRPhotoSelectUtils? = null
    var left_bitmap: Bitmap? = null
    var right_bitmap: Bitmap? = null
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
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
                iv1.setImageBitmap(right_bitmap)

            }, false)
            mLqrPhotoSelectUtils!!.takePhoto()
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_update_company
    }
}
