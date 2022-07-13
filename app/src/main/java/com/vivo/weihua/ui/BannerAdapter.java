package com.vivo.weihua.ui;

import android.view.View;

public abstract class BannerAdapter {
    public abstract View getView(int position, View convertView);

    public abstract int getContent();

    public abstract int test(int index);
}
