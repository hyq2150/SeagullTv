package com.victor.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;

import com.victor.seagull.ProgramDetailActivity;
import com.victor.seagull.R;
import com.victor.view.ColorTextView;

import butterknife.Bind;
import butterknife.OnClick;

public class ColorFragment extends BaseFragment {

    @Bind(R.id.ctv_title)
    ColorTextView mCtvTitle;
    @Bind(R.id.image)
    ImageView mIvImg;
    @Override
    protected int getLayoutResource() {
        return R.layout.frag_color;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnClick({R.id.image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                Intent intent = new Intent(getActivity(),ProgramDetailActivity.class);
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                view, getString(R.string.transition_album_img));

                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                break;
        }

    }

}