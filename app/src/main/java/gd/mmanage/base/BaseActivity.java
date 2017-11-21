package gd.mmanage.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.arialyy.frame.core.AbsActivity;

/**
 * Created by lyy on 2016/7/13.
 * https://github.com/AriaLyy/MVVM
 */
public abstract class BaseActivity<VB extends ViewDataBinding> extends AbsActivity<VB> {
    Toolbar mBar;
    Toast toast;
    String [] s;
    public AlertDialog.Builder builder;

    public void toast(String msg) {
        s=new String[10];
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        builder = new AlertDialog.Builder(this);
    }

    @Override
    protected void dataCallback(int result, Object data) {

    }
}
