package com.example.accessibility;


import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

/**
 * Created by zhangheng1 on 2018/10/24.
 */

public class MyAccessibilityService extends BaseAccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    public static boolean step0 = false;
    private boolean step1 = false;
    private boolean step2 = false;
    private boolean step3 = false;
    private boolean step4 = false;
    private boolean step5 = false;

    public static boolean s0 = false;
    private boolean s1 = false;
    private boolean s2 = false;
    private boolean s3 = false;
    private boolean s4 = false;
    private boolean s5 = false;
    public static String contact = "zh";
    public static String content = "test";

    public static ArrayList<EventBeen> eventBeens = new ArrayList<>();
    public static boolean isStartRecord = false;
    public static boolean isStartPlay = false;
    boolean begin = false;
    private int index = 0;
    private Instrumentation mInst = new Instrumentation();
    private Handler handler = new Handler();
    private long delay = 0;
    public static boolean begin1 = false;
    private AccessibilityNodeInfo myRootInActiveWindow;
    public static EventBeen eventBeen = null;

    private void launch(String pak) {


        Intent intent1 = getPackageManager().getLaunchIntentForPackage(pak);
        intent1.setFlags(FLAG_ACTIVITY_CLEAR_TASK);
//        intent1.setClassName(pak, classname);
        context.startActivity(intent1);
    }

    private void launch(String pak, String classname) {


        Intent intent1 = getPackageManager().getLaunchIntentForPackage(pak);
        intent1.setFlags(FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setClassName(pak, classname);
        context.startActivity(intent1);
    }



    @SuppressLint("WrongConstant")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.d(TAG, "onAccessibilityEvent: " + event.toString() + " Action: " + event.getAction());
        //点外卖
//        (TYPE_WINDOW_STATE_CHANGED PackageName)(TYPE_VIEW_CLICKED getViewIdResourceName event.getText()event.getContentDescription())

//        Log.d(TAG, "onAccessibilityEvent: ididididiidididid" + (event.getSource() == null ? "null" : event.getSource().getViewIdResourceName()));
        AccessibilityNodeInfo source2 = event.getSource();
        Log.d(TAG, "onAccessibilityEvent: 000000     " + event.getEventType() +  " source " + (source2 == null));


        if (isStartRecord) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                    !event.getPackageName().equals("com.meizu.flyme.launcher") &&
                    !event.getPackageName().equals("com.miui.home") &&
                    !event.getPackageName().equals("com.example.accessibility") &&
                    !event.getPackageName().equals("com.android.systemui") &&
                    !((String) event.getClassName()).startsWith("android")) {
                eventBeen = new EventBeen(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                        null, event.getPackageName().toString(), event.getClassName().toString(), null);
                eventBeens.add(eventBeen);
                eventBeen = null;
                MainActivity.isStartRecord = true;
                Log.d(TAG, "onAccessibilityEvent: add eventBeens TYPE_WINDOW_STATE_CHANGED");
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                    !event.getPackageName().equals("com.miui.home") &&
                    !event.getPackageName().equals("com.android.systemui") &&
                    !event.getPackageName().equals("com.meizu.flyme.launcher")
                    ) {
                AccessibilityNodeInfo source = event.getSource();
                if (source != null) {
                    Rect rect = new Rect();
                    source.getBoundsInScreen(rect);
                    int x = (rect.left + rect.right) / 2;
                    int y = (rect.top + rect.bottom) / 2;
                    String viewIdResourceName = source.getViewIdResourceName();
                    String text1 = source.getText() == null ? null : source.getText().toString();
                    String contentDescription = source.getContentDescription() == null ? null : source.getContentDescription().toString();
                    String text2 = text1 == null ? (contentDescription == null ? null : contentDescription) : text1;
                    String text = text2 == null ? (String) (event.getText().size() == 0 ? null : event.getText().get(0).toString()) : text2;
                    eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_CLICKED, viewIdResourceName,
                            event.getPackageName().toString(), event.getClassName().toString(), text, x, y);

                } else if (event.getText().size() != 0 || event.getContentDescription() != null) {
                    AccessibilityNodeInfo viewByText = findViewByText(event.getContentDescription() == null ?
                            event.getText().get(0).toString() : event.getContentDescription().toString());
                    if (viewByText != null) {
                        eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_CLICKED, viewByText.getViewIdResourceName(),
                                event.getPackageName().toString(), event.getClassName().toString(),
                                event.getContentDescription() == null ? event.getText().get(0).toString() :
                                        event.getContentDescription().toString(),MainActivity.touchx, MainActivity.touchy);


                    } else {
                        eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_CLICKED, null,
                                event.getPackageName().toString(), event.getClassName().toString(),
                                event.getContentDescription() == null ? event.getText().get(0).toString() :
                                        event.getContentDescription().toString(),MainActivity.touchx, MainActivity.touchy);
                    }
                }
                Log.d(TAG, "onAccessibilityEvent: add eventBeens TYPE_VIEW_CLICKED");
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED &&
                    !event.getPackageName().equals("com.miui.home") &&
                    !event.getPackageName().equals("com.android.systemui") &&
                    !event.getPackageName().equals("com.meizu.flyme.launcher")
                    ) {
                AccessibilityNodeInfo source = event.getSource();
                if (source != null) {
                    String viewIdResourceName = source.getViewIdResourceName();
                    String text1 = source.getText() == null ? null : source.getText().toString();
                    String contentDescription = source.getContentDescription() == null ? null : source.getContentDescription().toString();
                    String text2 = text1 == null ? (contentDescription == null ? null : contentDescription) : text1;
                    String text = text2 == null ? (String) (event.getText().size() == 0 ? null : event.getText().get(0)) : text2;
                    eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED, viewIdResourceName,
                            event.getPackageName().toString(), event.getClassName().toString(), text);

                } else if (event.getText().size() != 0) {
                    eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED, null,
                            event.getPackageName().toString(), event.getClassName().toString(), (String) event.getText().get(0));

                }
                Log.d(TAG, "onAccessibilityEvent: add eventBeens TYPE_VIEW_TEXT_CHANGED");
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED &&
                    !event.getPackageName().equals("com.miui.home") &&
                    !event.getPackageName().equals("com.android.systemui") &&
                    !event.getPackageName().equals("com.meizu.flyme.launcher")
                    ) {
                AccessibilityNodeInfo source = event.getSource();
                if (source != null) {
                    Rect rect = new Rect();
                    source.getBoundsInScreen(rect);
                    int x = (rect.left + rect.right) / 2;
                    int y = (rect.top + rect.bottom) / 2;
                    String viewIdResourceName = source.getViewIdResourceName();
                    String text1 = source.getText() == null ? null : source.getText().toString();
                    String contentDescription = source.getContentDescription() == null ? null : source.getContentDescription().toString();
                    String text2 = text1 == null ? (contentDescription == null ? null : contentDescription) : text1;
                    String text = text2 == null ? (String) (event.getText().size() == 0 ? null : event.getText().get(0)) : text2;
                    eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_FOCUSED, viewIdResourceName,
                            event.getPackageName().toString(), event.getClassName().toString(), text, x, y);
                } else if (event.getText().size() != 0) {
                    AccessibilityNodeInfo viewByText = findViewByText(event.getContentDescription() == null ?
                            event.getText().get(0).toString() : event.getContentDescription().toString());
                    Log.d(TAG, "myRootInActiveWindow find: " + (viewByText == null));
                    if (viewByText != null) {
                        eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_FOCUSED, viewByText.getViewIdResourceName(),
                                event.getPackageName().toString(), event.getClassName().toString(),
                                event.getContentDescription() == null ? event.getText().get(0).toString() :
                                        event.getContentDescription().toString(),MainActivity.touchx, MainActivity.touchy);


                    } else {
                        eventBeen = new EventBeen(AccessibilityEvent.TYPE_VIEW_FOCUSED, null,
                                event.getPackageName().toString(), event.getClassName().toString(),
                                event.getContentDescription() == null ? event.getText().get(0).toString() :
                                        event.getContentDescription().toString(),MainActivity.touchx, MainActivity.touchy);
                    }
                }
                Log.d(TAG, "onAccessibilityEvent: add eventBeens TYPE_VIEW_FOCUSED");
            }
        }

        if (isStartPlay) {
            Log.d(TAG, "onAccessibilityEvent: eventBeenseventBeens size" + eventBeens.size() + "   " + eventBeens.toString());
            for (int i = 0; i < eventBeens.size(); i++) {
                if (eventBeens.get(i).getEvent() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                        && !eventBeens.get(i).getPackagename().equals("com.meizu.flyme.launcher")
                        && !eventBeens.get(i).getClassname().startsWith("android")) {
                    try {
                        Log.d(TAG, "onAccessibilityEvent: launch");
                        launch(eventBeens.get(i).getPackagename());
                    } catch (Exception e) {
                        Log.d(TAG, "onAccessibilityEvent: 启动异常" + e.toString());
                    }

                    begin = true;
                    index = i + 1;
                    begin1 = true;
                    isStartPlay = false;
                    break;
                }

            }
            isStartPlay = false;
        }
        Log.d(TAG, "onAccessibilityEvent: 111111   " + event.getEventType());



        if (begin1 && eventBeens.size() != 0 && index < eventBeens.size()) {
            Log.d(TAG, "onAccessibilityEvent: 33333   " + event.getEventType());
            //自动点击
            if ((event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED ||
                    event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                    event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
                    event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED)
                    ) {
                Log.d(TAG, "onAccessibilityEvent: TYPE_VIEW_CLICKED  " + index);
                if (begin1) {
                    begin1 = false;

                }else {
                    return;
                }
                new Thread(){
                    public void run(){
                        for (int i = index; i < eventBeens.size(); i++) {
                            if (eventBeens.get(i).getEvent() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                                    !eventBeens.get(i).getPackagename().equals("com.meizu.flyme.launcher")) {
                                if (eventBeens.get(i).getId() != null) {
                                    final AccessibilityNodeInfo viewByID = findViewByOnlyIDAndText(eventBeens.get(i).getId(), eventBeens.get(i).getText());
                                    if (viewByID == null) {
                                        execShellCmd1("input tap " + eventBeens.get(i).getX() + " " + eventBeens.get(i).getY());

                                    } else {
                                        performViewClick(viewByID);
                                        Log.d(TAG, "onAccessibilityEvent: 自动点击  " + i + "  " + (viewByID == null));
                                    }
                                } else if (eventBeens.get(i).getText() != null) {
                                    final AccessibilityNodeInfo viewByID = findViewByTextOnlyOne(eventBeens.get(i).getText());
                                    if (viewByID == null) {
                                        execShellCmd1("input tap " + eventBeens.get(i).getX() + " " + eventBeens.get(i).getY());
                                    }else {
                                        performViewClick(viewByID);
                                        Log.d(TAG, "onAccessibilityEvent: 自动点击  " + i + "  " + (viewByID == null));
                                    }

                                } else {
                                    execShellCmd1("input tap " + eventBeens.get(i).getX() + " " + eventBeens.get(i).getY());
                                    Log.d(TAG, "onAccessibilityEvent: TYPE_VIEW_CLICKED nullllll");
                                }
                                index = i + 1;


                            } else if (eventBeens.get(i).getEvent() == AccessibilityEvent.TYPE_VIEW_FOCUSED &&
                                    !eventBeens.get(i).getPackagename().equals("com.meizu.flyme.launcher")) {
                                if (eventBeens.get(i).getX() == 0 && eventBeens.get(i).getY() == 0) {
                                    if (eventBeens.get(i).getId() != null) {
                                        final AccessibilityNodeInfo viewByID = findViewByOnlyIDAndText(eventBeens.get(i).getId(), eventBeens.get(i).getText());
                                        performViewClick(viewByID);
                                        Log.d(TAG, "onAccessibilityEvent: 自动点击  " + i + "  " + (viewByID == null));
                                    } else if (eventBeens.get(i).getText() != null) {
                                        final AccessibilityNodeInfo viewByID = findViewByText(eventBeens.get(i).getText());
                                        performViewClick(viewByID);
                                        Log.d(TAG, "onAccessibilityEvent: 自动点击  " + i + "  " + (viewByID == null));
                                    } else {
                                        Log.d(TAG, "onAccessibilityEvent: TYPE_VIEW_FOCUSED null");
                                    }
                                }else {
                                    execShellCmd1("input tap " + eventBeens.get(i).getX() + " " + eventBeens.get(i).getY());
                                }
                                index = i + 1;

                            } else if (eventBeens.get(i).getEvent() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED &&
                                    !eventBeens.get(i).getPackagename().equals("com.meizu.flyme.launcher")) {
                                if (eventBeens.get(i).getId() != null) {
                                    final AccessibilityNodeInfo viewByID = findViewByID(eventBeens.get(i).getId());
                                    performViewClick(viewByID);
                                    inputText(viewByID, eventBeens.get(i).getText());
                                    Log.d(TAG, "onAccessibilityEvent: TYPE_VIEW_TEXT_CHANGED  " + i + "  " + (viewByID == null));
                                } else if (eventBeens.get(i).getText() != null) {
                                    execShellCmd("input text " + eventBeens.get(i).getText());
                                    final AccessibilityNodeInfo viewByID = findViewByText(eventBeens.get(i).getText());
//                                    performViewClick(viewByID);
//                                    inputText(viewByID, eventBeens.get(i).getText());
                                    Log.d(TAG, "onAccessibilityEvent: TYPE_VIEW_TEXT_CHANGED  " + i + "  " + (viewByID == null));
                                } else {
                                    Log.d(TAG, "onAccessibilityEvent: TYPE_VIEW_TEXT_CHANGED nulll");
                                    execShellCmd("input text " + eventBeens.get(i).getText());
                                }
                                index = i + 1;

                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

            }
        }





        //自动发送微信
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals("com.tencent.mm") && s0) {
            CharSequence className = event.getClassName();
            if (className.equals("com.tencent.mm.ui.LauncherUI")) {

                AccessibilityNodeInfo nodeInfo = findViewByText("搜索");

                if (nodeInfo == null) {
                    Log.d(TAG, "onAccessibilityEvent: nodeInfo进入搜索为空");
                }
                if (nodeInfo != null) {
                    Log.d(TAG, "onAccessibilityEvent: 进入搜索");
                    performViewClick(nodeInfo);
                }
                s0 = false;
                s1 = true;
                s2 = false;
                s3 = false;
                s4 = false;
                s5 = false;
            }

        }

        if (s1) {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                    event.getPackageName().equals("com.tencent.mm")) {
                CharSequence className = event.getClassName();
                if (className.equals("android.widget.TextView")) {

                    AccessibilityNodeInfo nodeInfo = findViewByID("com.tencent.mm:id/ji");
                    AccessibilityNodeInfo rootNode1 = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> listview = rootNode1.findAccessibilityNodeInfosByText("搜索");
//                    AccessibilityNodeInfo nodeInfo = findViewByText("搜索");

                    if (nodeInfo == null) {
                        Log.d(TAG, "onAccessibilityEvent: nodeInfo输入联系人为空");
                    }
                    if (nodeInfo != null) {
                        Log.d(TAG, "onAccessibilityEvent: 开始输入联系人点击");
                        inputText(nodeInfo, contact);
                    }
                    s0 = false;
                    s1 = false;
                    s2 = true;
                    s3 = false;
                    s4 = false;
                    s5 = false;
                }
            }

        }


        if (s2) {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED &&
                    event.getPackageName().equals("com.tencent.mm")) {
                CharSequence className = event.getClassName();
                if (className.equals("android.widget.EditText")) {

                    if (event.getText().get(0).toString().equals(contact)) {
//                        AccessibilityNodeInfo itemInfo = TraversalAndFindContacts();
                        AccessibilityNodeInfo itemInfo = TraversalAndFindContacts1();

                        if (itemInfo == null) {
                            Log.d(TAG, "onAccessibilityEvent: nodeInfo联系人为空");
                        }
                        if (itemInfo != null) {
                            Log.d(TAG, "onAccessibilityEvent: 开始联系人点击");
                            performViewClickParent3(itemInfo);
                        }
                    }
                    s0 = false;
                    s1 = false;
                    s2 = false;
                    s3 = true;
                    s4 = false;
                    s5 = false;
                }

            }

        }

        if (s3) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                    event.getPackageName().equals("com.tencent.mm")) {
                CharSequence className = event.getClassName();
                if (className.equals("com.tencent.mm.ui.chatting.ChattingUI")) {

                    AccessibilityNodeInfo nodeInfo = findViewByID("com.tencent.mm:id/aie");
                    if (nodeInfo == null) {
                        Log.d(TAG, "onAccessibilityEvent: nodeInfo发消息为空");
                    }
                    if (nodeInfo != null) {
                        Log.d(TAG, "onAccessibilityEvent: 进入发消息");
                        inputText(nodeInfo, content);
                        AccessibilityNodeInfo nodeInfo1 = findViewByText("发送");
                        if (nodeInfo1 == null) {
                            Log.d(TAG, "onAccessibilityEvent: nodeInfo111111发消息为空");
                        }
                        if (nodeInfo1 != null) {
                            Log.d(TAG, "onAccessibilityEvent:111111111 进入发消息");
                            performViewClick(nodeInfo1);
                        }
                    }
                    s0 = false;
                    s1 = false;
                    s2 = false;
                    s3 = false;
                    s4 = false;
                    s5 = false;
                }

            }
//            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
//                    event.getPackageName().equals("com.tencent.mm")) {
//                CharSequence className = event.getClassName();
//                if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
//
//                    AccessibilityNodeInfo nodeInfo = findViewByText("发消息");
//                    if (nodeInfo == null) {
//                        Log.d(TAG, "onAccessibilityEvent: nodeInfo发消息为空");
//                    }
//                    if (nodeInfo != null) {
//                        Log.d(TAG, "onAccessibilityEvent: 进入发消息");
//                        performViewClick(nodeInfo);
//                    }
//                    s1 = false;
//                    s2 = false;
//                    s3 = false;
//                    s4 = true;
//                    s5 = false;
//                }
//
//            }
        }

        if (s4) {
//            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
//                    event.getPackageName().equals("com.tencent.mm")) {
//                CharSequence className = event.getClassName();
//                if (className.equals("com.tencent.mm.ui.chatting.ChattingUI")) {
//
//                    AccessibilityNodeInfo nodeInfo = findViewByID("com.tencent.mm:id/aie");
//                    if (nodeInfo == null) {
//                        Log.d(TAG, "onAccessibilityEvent: nodeInfo发消息为空");
//                    }
//                    if (nodeInfo != null) {
//                        Log.d(TAG, "onAccessibilityEvent: 进入发消息");
//                        inputText(nodeInfo,"测试自动发消息");
//                        AccessibilityNodeInfo nodeInfo1 = findViewByText("发消息");
//                        if (nodeInfo1 == null) {
//                            Log.d(TAG, "onAccessibilityEvent: nodeInfo111111发消息为空");
//                        }
//                        if (nodeInfo1 != null) {
//                            Log.d(TAG, "onAccessibilityEvent:111111111 进入发消息");
//                            performViewClick(nodeInfo1);
//                        }
//                    }
//                    s1 = false;
//                    s2 = false;
//                    s3 = false;
//                    s4 = false;
//                    s5 = false;
//                }
//
//            }
        }


        //抖音看吃鸡
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals("com.ss.android.ugc.aweme") && step0) {
            CharSequence className = event.getClassName();
            if (className.equals("com.ss.android.ugc.aweme.main.MainActivity")) {

                AccessibilityNodeInfo nodeInfo = findViewByID("com.ss.android.ugc.aweme:id/ad4");
                if (nodeInfo == null) {
                    Log.d(TAG, "onAccessibilityEvent: nodeInfo为空");
                }
                if (nodeInfo != null) {
                    Log.d(TAG, "onAccessibilityEvent: 开始点击");
                    performViewClick(nodeInfo);
                }
                step0 = false;
                step1 = true;
                step2 = false;
                step3 = false;
                step4 = false;
                step5 = false;

            }
        }

        if (step1) {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                    event.getPackageName().equals("com.ss.android.ugc.aweme")) {
                CharSequence className = event.getClassName();
                if (className.equals("android.widget.ImageView")) {

                    AccessibilityNodeInfo nodeInfoinput = findViewByID("com.ss.android.ugc.aweme:id/a65");
                    if (nodeInfoinput == null) {
                        Log.d(TAG, "onAccessibilityEvent: nodeInfoinput为空");
                    }
                    if (nodeInfoinput != null) {
                        Log.d(TAG, "onAccessibilityEvent: 点击输入内容");
                        performViewClick(nodeInfoinput);
                    }
                    step1 = false;
                    step2 = true;
                    step3 = false;
                    step4 = false;
                    step5 = false;


                }
            }

        }


        if (step2) {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                    event.getPackageName().equals("com.ss.android.ugc.aweme")) {
                CharSequence className = event.getClassName();
                if (className.equals("android.widget.EditText")) {

                    AccessibilityNodeInfo nodeInfoinput = findViewByID("com.ss.android.ugc.aweme:id/a65");
                    if (nodeInfoinput == null) {
                        Log.d(TAG, "onAccessibilityEvent: nodeInfoinput为空");
                    }
                    if (nodeInfoinput != null) {
                        Log.d(TAG, "onAccessibilityEvent: 输入内容");
                        inputText(nodeInfoinput, "吃鸡");
                    }
                    step1 = false;
                    step2 = false;
                    step3 = true;
                    step4 = false;
                    step5 = false;

                }
            }
        }


        if (step3) {
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED &&
                    event.getPackageName().equals("com.ss.android.ugc.aweme")) {
                CharSequence className = event.getClassName();
                if (className.equals("android.widget.EditText")) {

                    AccessibilityNodeInfo nodeInfoDo = findViewByID("com.ss.android.ugc.aweme:id/a67");
                    AccessibilityNodeInfo rootNode1 = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> listview = rootNode1.findAccessibilityNodeInfosByText("吃鸡");
                    nodeInfoDo = listview.get(1);
//                    AccessibilityNodeInfo nodeInfo = findViewByText("搜索");
                    if (nodeInfoDo == null) {
                        Log.d(TAG, "onAccessibilityEvent: nodeInfoDo为空");
//                        execShellCmd("input keyevent 66");
//                        execShellCmd("input keyevent 66");
                    }
                    if (nodeInfoDo != null) {
                        Log.d(TAG, "onAccessibilityEvent: 开始搜索");
                        performViewClick(nodeInfoDo);
//                        execShellCmd("input keyevent 66");
                    }


                    step1 = false;
                    step2 = false;
                    step3 = false;
                    step4 = false;
                    step5 = false;
                }
            }
        }


//        Log.d(TAG, "onAccessibilityEvent: " + event.toString() + " Action: " + event.getAction());
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                event.getPackageName().equals("com.example.accessibility")) {
            CharSequence className = event.getClassName();
            if (className.equals("com.example.accessibility.MainActivity")) {
                new Thread() {
                    public void run() {
                        try {
                            Log.d(TAG, "currentThread: " + Thread.currentThread().toString());
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        AccessibilityNodeInfo nodeInfo = findViewByText("捕获内容");
                        AccessibilityNodeInfo btn_test = findViewByID("com.example.accessibility:id/btn_test");
                        if (nodeInfo == null && btn_test == null) {
                            Log.d(TAG, "onAccessibilityEvent: 为空");
                            return;
                        }
                        Log.d(TAG, "onAccessibilityEvent: " + nodeInfo.describeContents() + " " + nodeInfo.getViewIdResourceName() + " " + nodeInfo.getText() + " " + nodeInfo.toString());
                        if (nodeInfo != null) {
                            Log.d(TAG, "onAccessibilityEvent: 开始点击");
                            performViewClick(nodeInfo);
                        }

                    }
                }.start();


            }
        }

    }

    private List<String> allNameList = new ArrayList<>();
    private int mRepeatCount = 0;

    /**
     * 从头至尾遍历寻找联系人
     *
     * @return
     */
    private AccessibilityNodeInfo TraversalAndFindContacts1() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByText(contact);
        return listview.get(0);
    }

    /**
     * 从头至尾遍历寻找联系人
     *
     * @return
     */
    private AccessibilityNodeInfo TraversalAndFindContacts() {

        if (allNameList != null) allNameList.clear();

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bp0");

        //是否滚动到了底部
        boolean scrollToBottom = false;
        if (listview != null && !listview.isEmpty()) {
            while (true) {
                Log.d(TAG, "TraversalAndFindContacts: ");
                //获取当前屏幕上的联系人信息
                List<AccessibilityNodeInfo> nameList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/om");
                List<AccessibilityNodeInfo> itemList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/oj");
                AccessibilityNodeInfo parent1 = nameList.get(0).getParent();
                AccessibilityNodeInfo parent = itemList.get(1).getParent();
                int childCount = itemList.get(1).getChildCount();
                AccessibilityNodeInfo child = itemList.get(1).getChild(0);
                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if (i == 0) {
                            //必须在一个循环内，防止翻页的时候名字发生重复
                            mRepeatCount = 0;
                        }
                        AccessibilityNodeInfo itemInfo = itemList.get(i + 1);
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        Log.d(TAG, "nickname = " + nickname);
                        if (nickname.equals(contact)) {
                            return nodeInfo;
                        }
                        if (!allNameList.contains(nickname)) {
                            allNameList.add(nickname);
                        } else if (allNameList.contains(nickname)) {
                            Log.d(TAG, "mRepeatCount = " + mRepeatCount);
                            if (mRepeatCount == 3) {
                                //表示已经滑动到顶部了
                                if (scrollToBottom) {
                                    Log.d(TAG, "没有找到联系人");
                                    //此次发消息操作已经完成
                                    return null;
                                }
                                scrollToBottom = true;
                            }
                            mRepeatCount++;
                        }
                    }
                }

                if (!scrollToBottom) {
                    //向下滚动
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                } else {
                    return null;
                }

                //必须等待，因为需要等待滚动操作完成
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private static void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private static void execShellCmd1(String cmd) {

        try {
            Log.d(TAG, "execShellCmd1: " + cmd);
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            Log.d(TAG, "execShellCmd1: Throwable");
            t.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: ");
    }

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
