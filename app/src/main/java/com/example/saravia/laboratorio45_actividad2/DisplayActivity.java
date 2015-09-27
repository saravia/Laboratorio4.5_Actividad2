package com.example.saravia.laboratorio45_actividad2;

import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.Intents.Insert;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayActivity  extends AppCompatActivity {
    ListView listContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Inside isplayActivity......");
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String buttonType = intent.getStringExtra("com.ts.contentprovider.NativeContentProvider.Button");
        setContentView(R.layout.activity_display);
        System.out.println("About to call displayContacts......");

        displayContacts(buttonType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void displayContacts(final String buttonType) {
        listContent = (ListView)findViewById(R.id.contentlist);
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        System.out.println("Inside 1"+cur.getCount());

        System.out.println("ContactsContract.Contacts._ID - "+ContactsContract.Contacts._ID);
        System.out.println("ContactsContract.Contacts.DISPLAY_NAME - "+ContactsContract.Contacts.DISPLAY_NAME);
        System.out.println("R.id.name, R.id.code - "+R.id.name+" "+R.id.code);
        System.out.println("cur - "+cur.getColumnCount());
        ListAdapter adapter = new android.widget.SimpleCursorAdapter(this, // Context.
                R.layout.activity_display,
                //android.R.layout.simple_list_item_2,
                cur,
                new String[] {
                        //ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME
                },
                //  new int[] { android.R.id.list },
                //   new int[] { R.id.contentlist },
                // new int[] { R.id.name, R.id.code },
                new int[] { R.id.name },
                //     new int[] {android.R.id.text1,android.R.id.text2},
                0);
        System.out.println("Inside 2"+adapter.getCount());

        //	setListAdapter(adapter);

        listContent.setAdapter(adapter);

        System.out.println("Inside 2.1 setting adapter");


        listContent.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //	Object o = parent.getAdapter().getItem(position);
                //	Object o = listContent.getAdapter().getItem(position);


                Cursor mycursor = (Cursor) parent.getItemAtPosition(position);
                String keyword = mycursor.getString(mycursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //	Toast.makeText(getApplicationContext(),
                //  	      "Click ListItem Number " + position + "Value "+keyword, Toast.LENGTH_LONG)
                // 	      .show();

                if(buttonType.equalsIgnoreCase("ViewContacts")){
                    callContact(keyword);
                }else{
                    modifyContact(keyword);
                }

            }

        });




    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void modifyContact(String keyword){

        //Intent intent = new Intent(Intent.);
        //intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        //intent.putExtra(ContactsContract.Intents.Insert.NAME, keyword);
        //int PICK_CONTACT = 200;
        //this.startActivityForResult(intent, PICK_CONTACT);



        // 	Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        //	i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        //	i.putExtra(Insert.NAME, keyword);
        //i.putExtra(Insert.PHONE, "209384");
        //	startActivity(i);

        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setData(Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/" + keyword));
        startActivityForResult(i, 200);


    }

    public void callContact(String keyword){
        ContentResolver cr = getContentResolver();
        String where = ContactsContract.Data.DISPLAY_NAME + " = ? ";
        String[] params = new String[] {keyword};
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, where, params, null);
        if (cur.getCount() > 0) {
            System.out.println("Inside 3"+cur.getCount());

            cur.moveToNext();
            // 	while (cur.moveToNext()) {

            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            System.out.println("Inside 4 " +id+ " - "+name);

            if (Integer.parseInt(cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +" = ?",
                        new String[]{name}, null);
                while (pCur.moveToNext()) {
                    String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // Toast.makeText(DisplayActivity.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo)));
                }





                pCur.close();
            }
            //  	}


        }

    }

}

