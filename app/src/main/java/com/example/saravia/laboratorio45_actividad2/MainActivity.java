package com.example.saravia.laboratorio45_actividad2;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button viewBttn = (Button)findViewById(R.id.button);
        Button addBttn = (Button)findViewById(R.id.button2);
        Button modifyBttn = (Button)findViewById(R.id.button3);
        Button deleteBttn = (Button)findViewById(R.id.button4);


        viewBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //displayContacts();
                Intent displayIntent = new Intent(MainActivity.this,DisplayActivity.class);
                displayIntent.putExtra("com.ts.contentprovider.NativeContentProvider.Button", "ViewContacts");
                startActivity(displayIntent);

                Log.i("NativeContentProvider", "Completed Displaying Contact list");
            }
        });

        addBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                createContact("Sample Name", "123456789");
                Log.i("NativeContentProvider", "Created a new contact, of course hard-coded");
            }
        });

        modifyBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //	Intent displayIntent = new Intent(NativeContentProvider.this ,DisplayActivity.class);
                //	displayIntent.putExtra("com.ts.contentprovider.NativeContentProvider.Button", "UpdateContacts");
                //	startActivity(displayIntent);

                Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                //	i.putExtra(Insert.NAME, keyword);
                //i.putExtra(Insert.PHONE, "209384");
                startActivity(i);

                Log.i("NativeContentProvider", "Completed Displaying Contact list");
                //	updateContact("Sample Name", "987654321");
                Log.i("NativeContentProvider", "Completed updating the email id, if applicable");
            }
        });

        deleteBttn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DELETE);
                i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                //	i.putExtra(Insert.NAME, keyword);
                //i.putExtra(Insert.PHONE, "209384");
                startActivity(i);
                //deleteContact("Sample Name");
                Log.i("NativeContentProvider", "Deleted the selected contact");
            }
        });
    }

    private void displayContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }
        }
    }


    private void createContact(String name, String phone){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        // Just two examples of information you can send to pre-fill out data for the
        // user.  See android.provider.ContactsContract.Intents.Insert for the complete
        // list.
        //	intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        //	intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);

        // Send with it a unique request code, so when you get called back, you can
        // check to make sure it is from the intent you launched (ideally should be
        // some public static final so receiver can check against it)
        int PICK_CONTACT = 100;
        this.startActivityForResult(intent, PICK_CONTACT);
    }

 /*   private void createContact(String name, String phone) {
    	ContentResolver cr = getContentResolver();

    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

    	if (cur.getCount() > 0) {
        	while (cur.moveToNext()) {
        		String existName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        		if (existName.contains(name)) {
                	Toast.makeText(NativeContentProvider.this,"The contact name: " + name + " already exists", Toast.LENGTH_SHORT).show();
                	return;
        		}
        	}
    	}

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "accountname@gmail.com")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "com.google")
                .build());
        System.out.println("sql 0  "+ops.get(0).toString());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        System.out.println("sql 1  "+ops.get(1).toString());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());
        System.out.println("sql 2  "+ops.get(2).toString());

        try {
			cr.applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Toast.makeText(NativeContentProvider.this, "Created a new contact with name: " + name + " and Phone No: " + phone, Toast.LENGTH_SHORT).show();

    }
    */


    private void updateContact(String name, String phone) {
        ContentResolver cr = getContentResolver();

        String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ? AND " +
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";
        String[] params = new String[] {name,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)};

        Cursor phoneCur = managedQuery(ContactsContract.Data.CONTENT_URI, null, where, params, null);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        if ( (null == phoneCur)  ) {
            createContact(name, phone);
        } else {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, params)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA, phone)
                    .build());
        }

        phoneCur.close();

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(this, "Updated the phone number of 'Sample Name' to: " + phone, Toast.LENGTH_SHORT).show();
    }

    private void deleteContact(String name) {

        ContentResolver cr = getContentResolver();
        String where = ContactsContract.Data.DISPLAY_NAME + " = ? ";
        String[] params = new String[] {name};

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(where, params)
                .build());
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(this, "Deleted the contact with name '" + name +"'", Toast.LENGTH_SHORT).show();

    }
}