package gd.mmanage.control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.PostRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gd.mmanage.base.BaseModule;
import gd.mmanage.callback.LzyResponse;
import gd.mmanage.model.EnterpriseModel;
import gd.mmanage.model.NormalRequest;
import okhttp3.Call;
import okhttp3.Response;
import wai.gr.cla.callback.JsonCallback;

/**
 * Created by Administrator on 2017/11/19.
 */

public class HttpUtil {

    public void gg(String url, final int back_id, HashMap<String, String> map, final BaseModule control) {
        PostRequest go = OkGo.post(url);
        if (map != null) {
            for (Map.Entry<String, String> model : map.entrySet()) {
                go.params(model.getKey(), model.getValue());
            }
        }
        go.execute(new StringCallback() {
            @Override
            public void onSuccess(String json, Call call, Response response) {
                LzyResponse t = new Gson().fromJson(json, LzyResponse.class);
                if (t.getSuccess()) {
                    control.callback(back_id, new NormalRequest(0, "", new JsonParser().parse(json).getAsJsonObject().get("Data")));
                } else {
                    control.callback(back_id, new NormalRequest(1, "当前无数据", null));
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                control.callback(back_id, new NormalRequest(3, "未知错误：" + e.toString(), null));
            }
        });

    }
}
