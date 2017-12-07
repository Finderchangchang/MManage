package gd.mmanage.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

import com.youth.banner.Banner
import gd.mmanage.R
import gd.mmanage.base.BaseFragment
import gd.mmanage.databinding.FragMainBinding
import gd.mmanage.method.GlideImageLoader
import gd.mmanage.ui.employee.SearchEmployeeActivity
import gd.mmanage.ui.inbound.SearchInBoundsActivity
import gd.mmanage.ui.notice.SearchNoticeActivity
import gd.mmanage.ui.vehicle.OnlySearchVehicleActivity
import gd.mmanage.ui.vehicle.SearchVehicleActivity

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
    var ll7: LinearLayout? = null
    var ll8: LinearLayout? = null

    var banner: Banner? = null
    override fun load_view(view: View?) {
        ll1 = view!!.findViewById(R.id.ll1) as LinearLayout
        ll2 = view.findViewById(R.id.ll2) as LinearLayout
        ll3 = view.findViewById(R.id.ll3) as LinearLayout
        ll4 = view.findViewById(R.id.ll4) as LinearLayout
        ll5 = view.findViewById(R.id.ll5) as LinearLayout
        ll6 = view.findViewById(R.id.ll6) as LinearLayout
        ll7 = view.findViewById(R.id.ll7) as LinearLayout
        ll8 = view.findViewById(R.id.ll8) as LinearLayout

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
        //车辆承接
        ll1!!.setOnClickListener {
            //            startActivity(Intent(context, AddPersonActivity::class.java)
            //           .putExtra("model", VehicleModel()))
            startActivity(Intent(context, SearchVehicleActivity::class.java))
        }
        //取车登记
        ll2!!.setOnClickListener {
            startActivity(Intent(context, OnlySearchVehicleActivity::class.java)
                    .putExtra("id", "1"))
        }
        //机修登记
        ll3!!.setOnClickListener {
            startActivity(Intent(context, OnlySearchVehicleActivity::class.java)
                    .putExtra("id", "2"))
        }
        //可疑车辆
        ll4!!.setOnClickListener {
            startActivity(Intent(context, OnlySearchVehicleActivity::class.java)
                    .putExtra("id", "3"))
        }
        //配件管理
        ll5!!.setOnClickListener {
            startActivity(Intent(context, SearchInBoundsActivity::class.java))
        }
        //人员管理
        ll6!!.setOnClickListener {
            startActivity(Intent(context, SearchEmployeeActivity::class.java))
        }
        //配件业务添加
        ll7!!.setOnClickListener {
            startActivity(Intent(context, OnlySearchVehicleActivity::class.java)
                    .putExtra("id", "4"))
        }
        //通知通告
        ll8!!.setOnClickListener {
            startActivity(Intent(context, SearchNoticeActivity::class.java))
        }
//        //从业人员
//        ll1!!.setOnClickListener {
//            startActivity(Intent(context, SearchEmployeeActivity::class.java))
//        }
//        //配件管理(入库单)
//        ll2!!.setOnClickListener {
//            startActivity(Intent(context, SearchInBoundsActivity::class.java))
//        }
//        //维修业务
//        ll3!!.setOnClickListener {
//            startActivity(Intent(context, AddServiceActivity::class.java)
//                    .putExtra("vehicleId", "C02130602000120171202001"))
//        }
//        //取车登记
//        ll4!!.setOnClickListener {
//            startActivity(Intent(context, AddGetCarActivity::class.java)
//                    .putExtra("vehicleId", "C02130602000120171202001"))
//        }
//        //车辆承接
//        ll5!!.setOnClickListener {
//            startActivity(Intent(context, SearchVehicleActivity::class.java))
//        }
//        //可疑车辆登记
//        ll6!!.setOnClickListener {
//            startActivity(Intent(context, AddDubiousCarActivity::class.java)
//                    .putExtra("vehicleId", "C02130602000120171202001"))
//        }
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