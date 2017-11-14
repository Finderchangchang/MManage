package gd.mmanage.ui.car_manage

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import gd.mmanage.R
import gd.mmanage.base.BaseActivity
import gd.mmanage.databinding.ActivityAddCarBinding

class AddServiceActivity : BaseActivity<ActivityAddCarBinding>() {

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        //身份证阅读器读卡
        var list: ArrayList<String> = ArrayList()
        list.add("奔腾-京Q123456")
        list.add("奔腾-京Q123456")
        list.add("奔腾-京Q123456")
        id_card_read_btn.setOnClickListener {
            user_detail_ll.visibility = View.VISIBLE
            read_btn_ll.visibility = View.GONE
            if (list.size > 0) {
                var builder = AlertDialog.Builder(this);
                builder.setTitle("已存在车辆");
                builder.setItems(arrayOf("奔腾-京Q123456", "奔腾-京Q123456", "奔腾-京Q123456")) { a, b ->
                    card_read_ll.visibility = View.GONE
                    card_detail_ll.visibility = View.VISIBLE
                }
                builder.show();
            }
        }
    }

    /**
     * 设置资源布局
     */
    override fun setLayoutId(): Int {
        return R.layout.activity_add_car
    }
}