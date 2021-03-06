package com.victor.seagull;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.bumptech.glide.Glide;
import com.mikepenz.foundation_icons_typeface_library.FoundationIcons;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.victor.fragment.ColorFragment;
import com.victor.fragment.ProgramFragment;
import com.victor.module.Player;
import com.victor.util.Constant;
import com.victor.util.DataCleanManager;
import com.victor.util.SharePreferencesUtil;
import com.victor.util.SystemUtils;
import com.victor.util.ThemeUtils;
import com.victor.view.ColorRelativeLayout;
import com.victor.view.ColorView;
import com.victor.view.PlayLayout;
import com.victor.view.ResideLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends BaseActivity implements ColorChooserDialog.ColorCallback {
    private String TAG = "MainActivity";
    private View view;
    @Bind(R.id.avatar)
    ImageView avatar;
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.resideLayout)
    ResideLayout resideLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().from(this).inflate(R.layout.activity_main,null);
        setContentView(view);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        ButterKnife.bind(this);

        Glide.with(MainActivity.this).load(R.mipmap.ic_avatar)
//                .placeholder(new IconicsDrawable(this)
//                        .icon(FoundationIcons.Icon.fou_photo)
//                        .color(Color.GRAY)
//                        .backgroundColor(Color.WHITE)
//                        .roundedCornersDp(40)
//                        .paddingDp(15)
//                        .sizeDp(75))
//                .bitmapTransform(new CropCircleTransformation(this))
//                .dontAnimate()
                .into(avatar);

        icon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_view_comfy).sizeDp(20));
        title.setText("从你的全世界路过");
        switchFragment(new ProgramFragment());

    }

    private void switchFragment(Fragment fragment) {
        if (currentFragment == null || !fragment.getClass().getName().equals(currentFragment.getClass().getName())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            currentFragment = fragment;
        }
    }


    @OnClick({R.id.avatar, R.id.ll_auto_update, R.id.ll_clear, R.id.ll_shake,
            R.id.ll_copyright, R.id.ll_about, R.id.ll_theme, R.id.icon,R.id.iv_share,R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                resideLayout.closePane();
                break;
            case R.id.ll_auto_update:
                resideLayout.closePane();
                Toast.makeText(getApplicationContext(),"已打开自动更新！",Toast.LENGTH_SHORT).show();
//                icon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_view_comfy).sizeDp(20));
//                title.setText("从你的全世界路过");
                break;
            case R.id.ll_clear:
                resideLayout.closePane();
                DataCleanManager.cleanApplicationData(this);
                Toast.makeText(getApplicationContext(),"已清除全部缓存！",Toast.LENGTH_SHORT).show();
//                icon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_mood).sizeDp(20));
//                title.setText(R.string.fuli);
//                switchFragment(new ColorFragment());
                break;
            case R.id.ll_shake:
                resideLayout.closePane();
                boolean isShakeChangeProgram = SharePreferencesUtil.getBoolean(this,Constant.IS_SHAKE_CHANGE_PROGRAM);
                SharePreferencesUtil.putBoolean(this,Constant.IS_SHAKE_CHANGE_PROGRAM,!isShakeChangeProgram);
                if (isShakeChangeProgram) {
                    Toast.makeText(getApplicationContext(),"已关闭摇一摇换台！",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"已打开摇一摇换台！",Toast.LENGTH_SHORT).show();
                }
//                icon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_android).sizeDp(20));
//                title.setText(R.string.android);
                break;
            case R.id.ll_copyright:
                resideLayout.closePane();
                new MaterialDialog.Builder(this)
                        .title(R.string.copyright)
                        .icon(new IconicsDrawable(this)
                                .color(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                                .icon(MaterialDesignIconic.Icon.gmi_account)
                                .sizeDp(20))
                        .content(R.string.copyright_content)
                        .positiveText(R.string.close)
                        .show();
//                icon.setImageDrawable(new IconicsDrawable(this).color(Color.WHITE).icon(MaterialDesignIconic.Icon.gmi_apple).sizeDp(20));
//                title.setText(R.string.ios);
                break;

            case R.id.ll_about:
                resideLayout.closePane();
                new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .icon(new IconicsDrawable(this)
                                .color(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                                .icon(MaterialDesignIconic.Icon.gmi_account)
                                .sizeDp(20))
                        .content(R.string.about_me)
                        .positiveText(R.string.close)
                        .show();
                break;
            case R.id.ll_theme:
                resideLayout.closePane();
                new ColorChooserDialog.Builder(this, R.string.theme)
                        .customColors(R.array.colors, null)
                        .doneButton(R.string.done)
                        .cancelButton(R.string.cancel)
                        .allowUserColorInput(false)
                        .allowUserColorInputAlpha(false)
                        .show();
                break;
            case R.id.icon:
                resideLayout.openPane();
                break;
            case R.id.iv_share:
                Intent intentshare = new Intent(Intent.ACTION_SEND);
                intentshare.setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, "分享")
                        .putExtra(Intent.EXTRA_TEXT,"给你分享一个开源视频应用：" + "https://github.com/Victor2018/SeagullTv/raw/master/app/SeagullTv.apk");
                Intent.createChooser(intentshare, "分享");
                startActivity(intentshare);
                break;
            case R.id.fab:
                new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .icon(new IconicsDrawable(this)
                                .color(ThemeUtils.getThemeColor(this, R.attr.colorPrimary))
                                .icon(MaterialDesignIconic.Icon.gmi_account)
                                .sizeDp(20))
                        .content(R.string.about_me)
                        .positiveText(R.string.close)
                        .show();
                break;
        }
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        ThemeUtils.setTheme(this,selectedColor);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }


}
