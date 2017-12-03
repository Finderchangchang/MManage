package gd.mmanage.ui.inbound

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddInBoundBinding
import gd.mmanage.ui.parts.SearchPartsActivity
import kotlinx.android.synthetic.main.activity_add_in_bound.*
import net.tsz.afinal.view.DatePickerDialog

class AddInBoundActivity : BaseActivity<ActivityAddInBoundBinding>() {
    var datePickerDialog: DatePickerDialog? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        ll3.setOnClickListener {
            startActivityForResult(Intent(this, SearchPartsActivity::class.java), 11)
        }
        //选择入库时间
        ll9.setOnClickListener {
            datePickerDialog = DatePickerDialog(this, "")
            datePickerDialog!!.datePickerDialog(tv9)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 13) {

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_add_in_bound
    }
}
