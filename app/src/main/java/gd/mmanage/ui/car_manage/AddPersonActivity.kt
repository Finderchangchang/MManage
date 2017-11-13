package gd.mmanage.ui.car_manage

import android.content.Intent
import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddPersonBinding
import kotlinx.android.synthetic.main.activity_add_person.*

/**
 * 人员添加
 * */
class AddPersonActivity : BaseActivity<ActivityAddPersonBinding>() {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        left_btn.setOnClickListener { finish() }
        right_btn.setOnClickListener {
            startActivity(Intent(this@AddPersonActivity, AddCarActivity::class.java))
        }
        //证件识别操作
        read_card_btn.setOnClickListener {
            //身份证读取
            if (id_card_rb.isChecked) {

            } else {//orc读取驾驶证

            }
        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_person
    }
}
