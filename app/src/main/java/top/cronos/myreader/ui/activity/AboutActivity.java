/*
 * This file is part of QYReader.
 * QYReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QYReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QYReader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package top.cronos.myreader.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import top.cronos.myreader.R;
import top.cronos.myreader.application.App;
import top.cronos.myreader.base.BaseActivity;
import top.cronos.myreader.base.observer.MyObserver;
import top.cronos.myreader.common.APPCONST;
import top.cronos.myreader.common.URLCONST;
import top.cronos.myreader.databinding.ActivityAboutBinding;
import top.cronos.myreader.model.user.UserService;
import top.cronos.myreader.ui.dialog.DialogCreator;
import top.cronos.myreader.ui.dialog.LoadingDialog;
import top.cronos.myreader.ui.dialog.MyAlertDialog;
import top.cronos.myreader.util.ShareUtils;
import top.cronos.myreader.util.SharedPreUtils;
import top.cronos.myreader.util.ToastUtils;
import top.cronos.myreader.util.ZipUtils;
import top.cronos.myreader.util.utils.AdUtils;
import top.cronos.myreader.util.utils.FileUtils;
import top.cronos.myreader.util.utils.OkHttpUtils;
import top.cronos.myreader.util.utils.RxUtils;
import top.cronos.myreader.webapi.LanZouApi;

/**
 * @author fengyue
 * @date 2020/9/18 22:21
 */
public class AboutActivity extends BaseActivity<ActivityAboutBinding> {

    @Override
    protected void bindView() {
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        setStatusBarColor(R.color.colorPrimary, true);
        getSupportActionBar().setTitle("关于");
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        binding.il.tvVersionName.setText(String.format("起源阅读v%s", App.getStrVersionName()));
//        binding.il.rlLanZou.setVisibility(App.isDebug() ? View.VISIBLE : View.GONE);
        binding.il.rlResetPangle.setVisibility(App.isDebug() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initClick() {
        super.initClick();
        ClipboardManager mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        binding.il.rlUpdate.setOnClickListener(v -> App.checkVersionByServer(this, true));
        binding.il.rlUpdateLog.setOnClickListener(v -> DialogCreator.createAssetTipDialog(this, "更新日志", "updatelog.fy"));

        binding.il.rlShare.setOnClickListener(v -> ShareUtils.share(this, getString(R.string.share_text) +
                SharedPreUtils.getInstance().getString(getString(R.string.downloadLink), URLCONST.LAN_ZOU_URL)));
        /*binding.il.rlQq.setOnClickListener(v -> {
            if (!App.joinQQGroup(this, "8PIOnHFuH6A38hgxvD_Rp2Bu-Ke1ToBn")) {
                //数据
                ClipData mClipData = ClipData.newPlainText("Label", "1085028304");
                //把数据设置到剪切板上
                mClipboardManager.setPrimaryClip(mClipData);
                ToastUtils.showError("未安装手Q或安装的版本不支持！\n已复制QQ群号，您可自行前往QQ添加！");
            }
        });

        binding.il.rlOfficialWeb.setOnClickListener(v -> {
            MyAlertDialog.showFullWebViewDia(this, URLCONST.OFFICIAL_WEB,
                    true, null);
        });
        binding.il.rlGit.setOnClickListener(v -> {
            MyAlertDialog.showFullWebViewDia(this, getString(R.string.this_github_url),
                    true, null);
        });

        binding.il.rlContactAuthor.setOnClickListener(v -> {
            //数据
            ClipData mClipData = ClipData.newPlainText("Label", "fengyuecanzhu@gmail.com");
            //把数据设置到剪切板上
            mClipboardManager.setPrimaryClip(mClipData);
            ToastUtils.showSuccess("邮箱\"fengyuecanzhu@gmail.com\"已复制到剪切板");
        });
        binding.il.rlJoinQqChannel.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://qun.qq.com/qqweb/qunpro/share?_wv=3&_wwv=128&inviteCode=2aP6ZQ&from=246610&biz=ka"));
            startActivity(intent);
        });*/

        binding.il.rlShareLog.setOnClickListener(v -> DialogCreator.createCommonDialog(this, "分享崩溃日志",
                "你是希望将日志上传到服务器，还是直接分享给他人？", true,
                "上传服务器",  (dialog, which) -> uploadCrashLog()));

        binding.il.rlPrivacyPolicy.setOnClickListener(v -> MyAlertDialog
                .showFullWebViewDia(this, "file:///android_asset/PrivacyPolicy.html",
                        false, null));
        binding.il.rlDisclaimer.setOnClickListener(v -> DialogCreator.createAssetTipDialog(this, "免责声明", "disclaimer.fy"));
/*
        binding.il.rlLanZou.setOnClickListener(v -> {
            String[] str = new String[1];
            MyAlertDialog.createInputDia(this, getString(R.string.lan_zou_parse),
                    "格式：链接+逗号+密码(没有密码就不用填)", "", true,
                    500, text -> str[0] = text, (dialog, which) -> {
                        LanZouApi.INSTANCE.getFileUrl(str[0])
                                .compose(RxUtils::toSimpleSingle)
                                .subscribe(new MyObserver<String>() {
                                    @Override
                                    public void onNext(@NonNull String s) {
                                        ToastUtils.showInfo(s);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(s));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        ToastUtils.showError("" + e.getLocalizedMessage());
                                    }
                                });
                    });
        });
*/
        binding.il.rlResetPangle.setOnClickListener(v -> AdUtils.resetPangleId());
    }

    void openIntent(String intentName, String address) {
        try {
            Intent intent = new Intent(intentName);
            intent.setData(Uri.parse(address));
            startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showError(e.getLocalizedMessage());
        }
    }


    private void uploadCrashLog() {
        File logDir = new File(APPCONST.LOG_DIR);
        if (!logDir.exists() || logDir.listFiles() == null || logDir.listFiles().length == 0) {
            ToastUtils.showWarring("没有日志文件");
            return;
        }
        final Disposable[] disposable = new Disposable[1];
        LoadingDialog dialog = new LoadingDialog(this, "正在上传", () -> {
            if (disposable[0] != null) disposable[0].dispose();
        });
        dialog.show();
        String time = String.valueOf(System.currentTimeMillis());
        String fileName = "log-" + time + ".zip";
        String logZip = FileUtils.getCachePath() + File.separator + fileName;
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            File zipFile = FileUtils.getFile(logZip);
            if (ZipUtils.zipFile(logDir, zipFile)) {
                emitter.onNext(OkHttpUtils.upload(
                        URLCONST.LOG_UPLOAD_URL + "?action=log" + UserService.INSTANCE.makeAuth(),
                        logZip, fileName));
            } else {
                emitter.onError(new Throwable("日志文件压缩失败"));
            }
            emitter.onComplete();
        }).compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
                addDisposable(d);
            }

            @Override
            public void onNext(@NotNull String s) {
                ToastUtils.showInfo(s);
                FileUtils.deleteFile(APPCONST.LOG_DIR);
                FileUtils.deleteFile(logZip);
                dialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showError(e.getLocalizedMessage());
                FileUtils.deleteFile(logZip);
                dialog.dismiss();
            }

        });
    }

    private void shareCrashLog() {
        File logDir = new File(APPCONST.LOG_DIR);
        if (!logDir.exists() || logDir.listFiles() == null || logDir.listFiles().length == 0) {
            ToastUtils.showWarring("没有日志文件");
            return;
        }
        Observable.create((ObservableOnSubscribe<File>) emitter -> {
            String time = String.valueOf(System.currentTimeMillis());
            String fileName = "log-" + time + ".zip";
            String logZip = FileUtils.getCachePath() + File.separator + fileName;
            File zipFile = FileUtils.getFile(logZip);
            if (ZipUtils.zipFile(logDir, zipFile)) {
                emitter.onNext(zipFile);
            } else {
                emitter.onError(new Throwable("日志文件压缩失败"));
            }
            emitter.onComplete();
        }).compose(RxUtils::toSimpleSingle).subscribe(new MyObserver<File>() {
            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(@NotNull File file) {
                ShareUtils.share(AboutActivity.this, file, "分享日志文件", "application/x-zip-compressed");
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showError(e.getLocalizedMessage());
            }

        });
    }
}
