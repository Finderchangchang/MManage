package gd.mmanage.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.zhy.autolayout.*;

import butterknife.ButterKnife;

public abstract class BaseCameraActivity extends AutoLayoutActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());

        preInitData();
    }

    protected abstract int getContentViewResId();

    protected abstract void preInitData();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
