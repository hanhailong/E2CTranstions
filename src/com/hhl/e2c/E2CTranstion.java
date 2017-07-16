package com.hhl.e2c;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HanHailong on 2017/7/16.
 */
public class E2CTranstion extends AnAction {

    private static final String API = "http://fanyi.youdao.com/openapi.do?" +
            "keyfrom=Skykai521&key=977124034&type=data&doctype=json&version=1.1&q=";

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here

        //一共分为3步
        //1.获取鼠标当前选中的单词
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (editor == null) return;

        SelectionModel selectionModel = editor.getSelectionModel();

        String selectionText = selectionModel.getSelectedText();
        if ("".equals(selectionText) || null == selectionText) return;


        String result = "翻译后的结果";
        //2.调用相关api得到翻译后的结果
        Observable.just(API + selectionText)
                .map(s -> HttpUtils.doGet(s))
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(String s) {
                        //3.显示翻译后的结果
                        showTranstionResult(editor, s);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //3.显示翻译后的结果
//        showTranstionResult(editor, result);
    }

    /**
     * 通过Popwindow的形式显示翻译后的结果
     *
     * @param editor
     * @param result
     */
    private void showTranstionResult(Editor editor, String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                JBPopupFactory jbPopupFactory = JBPopupFactory.getInstance();

                jbPopupFactory.createHtmlTextBalloonBuilder(result, null
                        , new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(5000)
                        .createBalloon()
                        .show(jbPopupFactory.guessBestPopupLocation(editor), Balloon.Position.below);
            }
        });
    }
}
