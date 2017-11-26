package gd.mmanage.ui.inbound

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddInBoundBinding

class AddInBoundActivity : BaseActivity<ActivityAddInBoundBinding>() {
    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
    }
    override fun setLayoutId(): Int {
        return R.layout.activity_add_in_bound
    }
}
