package com.liangxun.yuejiula.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.chat.*;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.activity.AddContactActivity;
import com.liangxun.yuejiula.huanxin.chat.activity.ChatActivity;
import com.liangxun.yuejiula.huanxin.chat.activity.ContactlistActivity;
import com.liangxun.yuejiula.huanxin.chat.activity.GroupsActivity;
import com.liangxun.yuejiula.huanxin.chat.adapter.ChatAllHistoryAdapter;
import com.liangxun.yuejiula.huanxin.chat.db.InviteMessgeDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;
import com.liangxun.yuejiula.huanxin.chat.my.MyEMConversation;
import com.liangxun.yuejiula.util.StringUtil;


import java.util.*;

/**
 * 分类
 */
public class HxFragment extends BaseFragment implements View.OnClickListener {
    private InputMethodManager inputMethodManager;
    private ListView listView;
    private ChatAllHistoryAdapter adapter;
    public RelativeLayout errorItem;
    public TextView errorText;
    private boolean hidden;
    private List<MyEMConversation> conversationList = new ArrayList<MyEMConversation>();
    private ImageView add_contact;
    //获得好友资料
    List<Emp> emps = new ArrayList<Emp>();
    private LinearLayout listViewHead;//头部
    private RelativeLayout dating;//大厅
    private RelativeLayout tongxunlu;//通讯录
    private TextView unread_qun;
    private TextView unread_shenqing;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_history, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        add_contact = (ImageView) getView().findViewById(R.id.add_contact);

        conversationList.addAll(loadConversationsWithRecentChat());
        listView = (ListView) getView().findViewById(R.id.list);

        //初始化ListView头部组件
        listViewHead = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.chatheader, null);
        unread_qun = (TextView) listViewHead.findViewById(R.id.unread_msg_number);
        unread_shenqing = (TextView) listViewHead.findViewById(R.id.unread_address_number);
        listView.addHeaderView(listViewHead);

        dating = (RelativeLayout) listViewHead.findViewById(R.id.dating);
        tongxunlu = (RelativeLayout) listViewHead.findViewById(R.id.tongxunlu);
        dating.setOnClickListener(this);
        tongxunlu.setOnClickListener(this);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList, getMyApp().getImageLoader());
        // 设置adapter
        listView.setAdapter(adapter);
        add_contact.setOnClickListener(this);
        getView().findViewById(R.id.add_group).setOnClickListener(this);
        getNickNamesByHxUserNames(getHxUsernames(conversationList));
        final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyEMConversation myConversation = adapter.getItem(position - 1);
                EMConversation conversation = myConversation.getEmConversation();
                Emp emp = myConversation.getEmp();

                String username = conversation.getUserName();
                if (username.equals(getMyApp().getUserName()))
                    Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if (conversation.isGroup()) {
                        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
                    } else {
                        // it is single chat
                        intent.putExtra("emp", emp);
                    }
                    startActivity(intent);
                }
            }
        });
        // 注册上下文菜单
        registerForContextMenu(listView);

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                hideSoftKeyboard();
                return false;
            }

        });
    }

    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (((AdapterView.AdapterContextMenuInfo) menuInfo).position > 0) {
            getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_message) {
            MyEMConversation tobeDeleteCons = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position - 1);
            // 删除此会话
            EMConversation em = tobeDeleteCons.getEmConversation();
            EMChatManager.getInstance().deleteConversation(em.getUserName(), em.isGroup());
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(em.getUserName());
            adapter.remove(tobeDeleteCons);
            adapter.notifyDataSetChanged();
            // 更新消息未读数
            ((MainActivity) getActivity()).updateUnreadLabel();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        EMConversation conversation = EMChatManager.getInstance().getConversation(getMyApp().getGroupId().trim());
        int qun = conversation.getUnreadMsgCount();
        Map<String, HxUser> users = getMyApp().getContactList();
        HxUser hxUser = users.get(HxConstant.NEW_FRIENDS_USERNAME);
        int unread = 0;
        if(hxUser!=null){
            unread = hxUser.getUnreadMsgCount();
        }
        if (unread > 0) {
            unread_shenqing.setText(String.valueOf(unread));
            unread_shenqing.setVisibility(View.VISIBLE);
        } else {
            unread_shenqing.setVisibility(View.INVISIBLE);
        }
        if (qun > 0) {
            unread_qun.setText(String.valueOf(qun));
            unread_qun.setVisibility(View.VISIBLE);
        } else {
            unread_qun.setVisibility(View.INVISIBLE);
        }
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        getNickNamesByHxUserNames(getHxUsernames(conversationList));
    }

    /**
     * 获取所有会话
     */
    private List<MyEMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<MyEMConversation> list = new ArrayList<MyEMConversation>();
        // 过滤掉messages seize为0的conversation
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() != 0) {
                MyEMConversation my = new MyEMConversation();
                if (!conversation.isGroup()) {
                    my.setEmConversation(conversation);
                    list.add(my);
                }
            }
        }
        // 排序
        sortConversationByLastChatTime(list);
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     */
    private void sortConversationByLastChatTime(List<MyEMConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<MyEMConversation>() {
            @Override
            public int compare(final MyEMConversation con1, final MyEMConversation con2) {

                EMMessage con2LastMessage = con2.getEmConversation().getLastMessage();
                EMMessage con1LastMessage = con1.getEmConversation().getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (((MainActivity) getActivity()).isConflict) {
            outState.putBoolean("isConflict", true);
        } else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
            outState.putBoolean(HxConstant.ACCOUNT_REMOVED, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_contact:
                Intent add_intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(add_intent);
                break;
            case R.id.dating:
//                if (getMyApp().getCurrentEmp() != null) {
//                    stepIntoGroup(getMyApp().getCurrentEmp());
//                }
            {
                Intent groupV = new Intent(getActivity(), GroupsActivity.class);
                startActivity(groupV);
            }
                break;
            case R.id.tongxunlu:
                Intent intent = new Intent(getActivity(), ContactlistActivity.class);
                startActivity(intent);
                break;
            case R.id.add_group:
                //dating
            {
                Intent groupV = new Intent(getActivity(), GroupsActivity.class);
                startActivity(groupV);
            }
                break;
        }
    }

    private void stepIntoGroup(Emp emp) {
        List<EMGroup> list=EMGroupManager.getInstance().getAllGroups();
        Boolean flag=false;
        for(EMGroup group:list){
            if(group.getGroupId().equals(emp.getGroupId())){
                flag=true;
            }
        }
        if (!flag) {
            addToGroup();
        } else {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
            intent.putExtra("groupId", getMyApp().getCurrentEmp().getGroupId().trim());
            startActivity(intent);
        }
    }

    //加群
    private void addToGroup() {
        //根据schooid获得groupid
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_USER_TO_GROUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                EMGroupManager.getInstance().loadAllGroups();
                            } else {
                                Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                        getMyApp().getCurrentEmp().setIsInGroup("1");
//                        stepIntoGroup(getMyApp().getCurrentEmp());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        EMGroupManager.getInstance().loadAllGroups();
                        Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("coid", getMyApp().getCurrentEmp().getSchoolId());
                params.put("hxUserName", getMyApp().getCurrentEmp().getHxUsername());
                params.put("empId", getMyApp().getCurrentEmp().getEmpId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    //通过环信username获取用户昵称
    private void getNickNamesByHxUserNames(final String hxUserNames) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                emps = data.getData();
                                notifyMyAdapter();
                            } else {
                                Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", hxUserNames);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);

    }


    private String getHxUsernames(List<MyEMConversation> conversationList) {
        StringBuffer strUser = new StringBuffer();
        for (int i = 0; i < conversationList.size(); i++) {
            strUser.append(conversationList.get(i).getEmConversation().getUserName());
            if (i < conversationList.size() - 1) {
                strUser.append(",");
            }
        }
        return strUser.toString();
    }

    //List<MyEMConversation> conversationList
    private void notifyMyAdapter() {

        for (MyEMConversation my : conversationList) {
            for (Emp emp : emps) {
                if (my.getEmConversation().getUserName().equals(emp.getHxUsername())) {
                    my.setEmp(emp);
                }
            }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }


}

