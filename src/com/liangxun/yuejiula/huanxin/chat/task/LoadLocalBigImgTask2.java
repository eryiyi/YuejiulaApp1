/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liangxun.yuejiula.huanxin.chat.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.easemob.util.ImageUtils;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.huanxin.chat.utils.ImageCache;
import com.liangxun.yuejiula.huanxin.chat.widget.photoview.PhotoView;

public class LoadLocalBigImgTask2 extends AsyncTask<Void, Void, Bitmap> {

    private ProgressBar pb;
    private ImageView photoView;
    private String path;
    private int width;
    private int height;
    private Context context;

    public LoadLocalBigImgTask2(Context context, String path, ImageView photoView,
                                ProgressBar pb, int width, int height) {
        this.context = context;
        this.path = path;
        this.photoView = photoView;
        this.pb = pb;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int degree = ImageUtils.readPictureDegree(path);
        if (degree != 0) {
            pb.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.INVISIBLE);
        } else {
            pb.setVisibility(View.INVISIBLE);
            photoView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = ImageUtils.decodeScaleImage(path, width, height);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        pb.setVisibility(View.INVISIBLE);
        photoView.setVisibility(View.VISIBLE);
        if (result != null)
            ImageCache.getInstance().put(path, result);
        else
            result = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.signin_local_gallry);
        photoView.setImageBitmap(result);
    }
}
