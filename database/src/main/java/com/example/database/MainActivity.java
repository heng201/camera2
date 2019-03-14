package com.example.database;


import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button btnReadAll;
    private Button btnReadNameByPhone;
    private Button btnAddContacts;
    private Button btnAddContactsInTransaction;
    private Button btnDelete;
    private Button btnUpdate;
    private TextView tvContent;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-10-18 14:15:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        btnReadAll = (Button)findViewById( R.id.btn_ReadAll );
        btnReadNameByPhone = (Button)findViewById( R.id.btn_ReadNameByPhone );
        btnAddContacts = (Button)findViewById( R.id.btn_AddContacts );
        btnAddContactsInTransaction = (Button)findViewById( R.id.btn_AddContactsInTransaction );
        btnDelete = (Button)findViewById( R.id.btn_Delete );
        btnUpdate = (Button)findViewById( R.id.btn_Update );
        tvContent = (TextView)findViewById( R.id.tv_content );

        btnReadAll.setOnClickListener( this );
        btnReadNameByPhone.setOnClickListener( this );
        btnAddContacts.setOnClickListener( this );
        btnAddContactsInTransaction.setOnClickListener( this );
        btnDelete.setOnClickListener( this );
        btnUpdate.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-10-18 14:15:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnReadAll ) {
            // Handle clicks for btnReadAll
            testReadAll();
        } else if ( v == btnReadNameByPhone ) {
            // Handle clicks for btnReadNameByPhone
            testReadNameByPhone();
        } else if ( v == btnAddContacts ) {
            // Handle clicks for btnAddContacts
            testAddContacts();
        } else if ( v == btnAddContactsInTransaction ) {
            // Handle clicks for btnAddContactsInTransaction
            try {
                testAddContactsInTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ( v == btnDelete ) {
            // Handle clicks for btnDelete
            try {
                testDelete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ( v == btnUpdate ) {
            // Handle clicks for btnUpdate
            try {
                testUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        MyListView lv_test = (MyListView) findViewById(android.R.id.list);
//        Child child1 = new Child();
//        Parent parent1 = child1;
//        parent1.sys();

    }
    //读取通讯录的全部的联系人
//需要先在raw_contact表中遍历id，并根据id到data表中获取数据
    public void testReadAll(){
        //uri = content://com.android.contacts/contacts
        Uri uri = Uri.parse("content://com.android.contacts/contacts"); //访问raw_contacts表
        ContentResolver resolver = this.getContentResolver();
        //获得_id属性
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts.Data._ID}, null, null, null);
        while(cursor.moveToNext()){
            StringBuilder buf = new StringBuilder();
            //获得id并且在data中寻找数据
            int id = cursor.getInt(0);
            buf.append("id="+id);
            uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");
            //data1存储各个记录的总数据，mimetype存放记录的类型，如电话、email等
            Cursor cursor2 = resolver.query(uri, new String[]{ContactsContract.Data.DATA1, ContactsContract.Data.MIMETYPE}, null,null, null);
            while(cursor2.moveToNext()){
                String data = cursor2.getString(cursor2.getColumnIndex("data1"));
                if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/name")){       //如果是名字
                    buf.append(",name="+data);
                }
                else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/phone_v2")){  //如果是电话
                    buf.append(",phone="+data);
                }
                else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/email_v2")){  //如果是email
                    buf.append(",email="+data);
                }
                else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/postal-address_v2")){ //如果是地址
                    buf.append(",address="+data);
                }
                else if(cursor2.getString(cursor2.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/organization")){  //如果是组织
                    buf.append(",organization="+data);
                }
            }
            String str = buf.toString();
            Log.i("Contacts", str);
            tvContent.setText(tvContent.getText() + str);
        }
    }

    /**
     * 查询
     */
    public void testReadNameByPhone(){
        String phone = "87654321";
        //uri=  content://com.android.contacts/data/phones/filter/#
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+phone);
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null); //从raw_contact表中返回display_name
        if(cursor.moveToFirst()){
            Log.i("Contacts", "name="+cursor.getString(0));
            tvContent.setText(cursor.getString(0));
        }

    }
    //一步一步添加数据
    public void testAddContacts(){
        //插入raw_contacts表，并获取_id属性
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        long contact_id = ContentUris.parseId(resolver.insert(uri, values));
        //插入data表
        uri = Uri.parse("content://com.android.contacts/data");
        //add Name
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/name");
        values.put("data2", "zdong");
        values.put("data1", "xzdong");
        resolver.insert(uri, values);
        values.clear();
        //add Phone
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");   //手机
        values.put("data1", "87654321");
        resolver.insert(uri, values);
        values.clear();
        //add email
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Contacts.Data.MIMETYPE,"vnd.android.cursor.item/email_v2");
        values.put("data2", "2");   //单位
        values.put("data1", "xzdong@xzdong.com");
        resolver.insert(uri, values);
    }

    /**
     * 插入
     * @throws Exception
     */
    public void testAddContactsInTransaction() throws Exception {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        // 向raw_contact表添加一条记录
        //此处.withValue("account_name", null)一定要加，不然会抛NullPointerException
        ContentProviderOperation operation1 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            operation1 = ContentProviderOperation
                    .newInsert(uri).withValue("account_name", null).build();
        }
        operations.add(operation1);
        // 向data添加数据
        uri = Uri.parse("content://com.android.contacts/data");
        //添加姓名
        ContentProviderOperation operation2 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            operation2 = ContentProviderOperation
                    .newInsert(uri).withValueBackReference("raw_contact_id", 0)
                    //withValueBackReference的第二个参数表示引用operations[0]的操作的返回id作为此值
                    .withValue("mimetype", "vnd.android.cursor.item/name")
                    .withValue("data2", "xzdong").build();
        }
        operations.add(operation2);
        //添加手机数据
        ContentProviderOperation operation3 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            operation3 = ContentProviderOperation
                    .newInsert(uri).withValueBackReference("raw_contact_id", 0)
                    .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
                    .withValue("data2", "2").withValue("data1", "0000000").build();
        }
        operations.add(operation3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            resolver.applyBatch("com.android.contacts", operations);
        }
    }

    /**
     * 删除
     * @throws Exception
     */
    public void testDelete()throws Exception{
        String name = "xzdong";
        //根据姓名求id
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Contacts.Data._ID},"display_name=?", new String[]{name}, null);
        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            //根据id删除data中的相应数据
            resolver.delete(uri, "display_name=?", new String[]{name});
            uri = Uri.parse("content://com.android.contacts/data");
            resolver.delete(uri, "raw_contact_id=?", new String[]{id+""});
        }
    }

    /**
     * 更新
     * @throws Exception
     */
    public void testUpdate()throws Exception{
        int id = 1;
        String phone = "999999";
        Uri uri = Uri.parse("content://com.android.contacts/data");//对data表的所有数据操作
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("data1", phone);
        resolver.update(uri, values, "mimetype=? and raw_contact_id=?", new String[]{"vnd.android.cursor.item/phone_v2",id+""});
    }
}
