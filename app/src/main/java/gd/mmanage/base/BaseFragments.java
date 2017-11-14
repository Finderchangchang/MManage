package gd.mmanage.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.arialyy.frame.core.AbsFragment;

/**
 * Created by Administrator on 2017/11/14.
 */
public abstract class BaseFragments<VB extends ViewDataBinding> extends AbsFragment<VB> {

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void onDelayLoad() {

    }

    @Override
    protected void dataCallback(int result, Object obj) {

    }
}