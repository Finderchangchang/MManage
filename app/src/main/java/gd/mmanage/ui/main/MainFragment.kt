package gd.mmanage.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

import butterknife.Bind
import com.youth.banner.Banner
import gd.mmanage.R
import gd.mmanage.base.BaseFragment
import gd.mmanage.base.BaseFragments
import gd.mmanage.databinding.FragMainBinding
import gd.mmanage.method.GlideImageLoader
import gd.mmanage.ui.car_manage.AddDubiousCarActivity
import gd.mmanage.ui.car_manage.AddPersonActivity
import gd.mmanage.ui.employee.SearchEmployeeActivity
import gd.mmanage.ui.parts.SearchPartsActivity
import kotlinx.android.synthetic.main.frag_main.view.*

/**
 * Created by Administrator on 2017/11/11.
 */

class MainFragment : BaseFragment<FragMainBinding>() {
    var ll1: LinearLayout? = null
    var ll2: LinearLayout? = null
    var ll3: LinearLayout? = null
    var ll4: LinearLayout? = null
    var ll5: LinearLayout? = null
    var ll6: LinearLayout? = null

    var banner: Banner? = null
    override fun load_view(view: View?) {
        ll1 = view!!.findViewById(R.id.ll1) as LinearLayout
        ll2 = view.findViewById(R.id.ll2) as LinearLayout
        ll3 = view.findViewById(R.id.ll3) as LinearLayout
        ll4 = view.findViewById(R.id.ll4) as LinearLayout
        ll5 = view.findViewById(R.id.ll5) as LinearLayout
        ll6 = view.findViewById(R.id.ll6) as LinearLayout
        banner = view.findViewById(R.id.banner) as Banner
    }

    //加载顶部轮播图
    fun load_banner() {
        var list: ArrayList<Int> = ArrayList()
        list.add(R.mipmap.banner)
        banner!!.setImages(list)
        banner!!.setImageLoader(GlideImageLoader())
        banner!!.start();
    }

    var main_context: HomeActivity? = null

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState);
        main_context = HomeActivity.context
        load_banner()
        //从业人员
        ll1!!.setOnClickListener {
            startActivity(Intent(context, SearchEmployeeActivity::class.java))
        }
        //配件管理
        ll2!!.setOnClickListener {
            startActivity(Intent(context, SearchPartsActivity::class.java))
        }
        //维修业务
        ll3!!.setOnClickListener {
            startActivity(Intent(context, AddPersonActivity::class.java))
        }
        //取车登记
        ll4!!.setOnClickListener {
            startActivity(Intent(context, AddPersonActivity::class.java))
        }
        //车辆承接
        ll5!!.setOnClickListener {
            startActivity(Intent(context, AddPersonActivity::class.java))
        }
        //可疑车辆登记
        ll6!!.setOnClickListener {
            startActivity(Intent(context, AddDubiousCarActivity::class.java))
        }
    }

    /**
     * 延时加载
     */
    override fun onDelayLoad() {

    }

    override fun setLayoutId(): Int {
        return R.layout.frag_main
    }


    override fun dataCallback(result: Int, obj: Any) {

    }

    companion object {
        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }
}