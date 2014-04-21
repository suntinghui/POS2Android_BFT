package com.bft.pos.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bft.pos.R;
import com.bft.pos.agent.client.ApplicationEnvironment;
import com.bft.pos.agent.client.Constant;
import com.bft.pos.dynamic.component.ViewException;
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.model.CityModel;
import com.bft.pos.util.Bank;
import com.bft.pos.util.BankParse;
import com.bft.pos.util.Province;
import com.bft.pos.util.ProvinceParse;

//实名认证 上图图片
public class AuthenticationUpImageActivity extends BaseActivity implements
		OnClickListener {

	// private ImageView iv_0;
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;

	// private String //bitmap_str_0 = null;
	private String bitmap_str_1 = null;
	private String bitmap_str_2 = null;
	private String bitmap_str_3 = null;

	private String merchant_id;

	private int current_index = 0;
	private Button btn_bank_branch;

	//
	private final int SING_CHOICE_DIALOG = 1;
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private ArrayList<String> bankNameList = new ArrayList<String>();
	private ArrayList<String> bankCodeList = new ArrayList<String>();
	private String selectBankName;
	private String selectBankCode;

	private ProvinceParse parse;
	private BankParse parse_bank;

	private Spinner spinner0, spinner1, spinner2;

	private Province currentProvince;

	private CityModel currentCity;
	private Bank currentBank;

	private EditText et_account = null;
	private EditText et_account_confirm = null;
	private EditText buss_name = null;

	private String merchant_name = null;
	private String mastername = null;

	private TextView tv_mastername = null;

	private String bankbranchid = null;
	private String bankbranchname = null;

	private EditText et_sms;
	private Button btn_sms, bt_confirm;

	private Context context;
	private Button btn_crop;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "Acount.png";
	private String[] items = new String[] { "本地图片", "拍照" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_authentication_upimage);

		Button btn_back = (Button) this.findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		btn_bank_branch = (Button) this.findViewById(R.id.btn_bank_branch);
		btn_bank_branch.setOnClickListener(this);

		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);

		bt_confirm = (Button) this.findViewById(R.id.bt_confirm);
		bt_confirm.setOnClickListener(this);

		parse = ProvinceParse.build(this, R.raw.province, R.raw.cities);
		parse_bank = BankParse.build(this, R.raw.banks);
		spinner0 = (Spinner) findViewById(R.id.spinner0);
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		ArrayAdapter<Bank> bankAdapter = new ArrayAdapter<Bank>(this,
				R.layout.simple_spinner_item, android.R.id.text1,
				parse_bank.getBanks());
		ArrayAdapter<Province> provinceAdapter = new ArrayAdapter<Province>(
				this, R.layout.simple_spinner_item, android.R.id.text1,
				parse.getProvinces());
		spinner0.setAdapter(bankAdapter);
		spinner1.setAdapter(provinceAdapter);

		spinner0.setOnItemSelectedListener(new BankAdapter());
		spinner1.setOnItemSelectedListener(new ProvinceAdapter());
		spinner2.setOnItemSelectedListener(new CityAdapter());

		Intent intent = this.getIntent();
		merchant_id = intent.getStringExtra("merchant_id");
		// iv_0 = (ImageView) this.findViewById(R.id.iv_0);
		iv_1 = (ImageView) this.findViewById(R.id.iv_1);
		iv_2 = (ImageView) this.findViewById(R.id.iv_2);
		iv_3 = (ImageView) this.findViewById(R.id.iv_3);

		// iv_0.setOnClickListener(this);
		iv_1.setOnClickListener(this);
		iv_2.setOnClickListener(this);
		iv_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.bt_confirm:
			try {
				Event event = new Event(null, "newidentifyMerchant", null);
				event.setTransfer("089021");
				String fsk = "Get_ExtPsamNo|null";
				event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("pIdImg0", bitmap_str_1);// 身份证正面
				map.put("pIdImg1", bitmap_str_2);// 身份证反面
				map.put("bankNo", "10");// 银行卡开户行<12
				map.put("bkCardNo", "11");// 银行卡号<19
				map.put("bkCardImg", bitmap_str_3);// 银行卡图片
				map.put("mctName", "山西");// 商户名
				map.put("verifyCode", "123456");// 验证码
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (ViewException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		case R.id.iv_1:
			current_index = 1;
			showDialog();
			// actionCamera();
			break;

		case R.id.iv_2:
			current_index = 2;
			showDialog();
			break;

		case R.id.iv_3:
			current_index = 3;
			showDialog();
			// actionCamera();
			break;
		// 银行
		case R.id.btn_bank_branch:
			Intent intent = new Intent(AuthenticationUpImageActivity.this,
					BankSearchActivity.class);
			intent.putExtra("bankCode", currentBank.getCode());
			intent.putExtra("provinceCode", currentCity.getProvince_code());
			intent.putExtra("cityCode", currentCity.getCode());
			AuthenticationUpImageActivity.this
					.startActivityForResult(intent, 1);
			break;
		// 短信验证
		case R.id.btn_sms:
			phoneverifcode();
			break;
		default:
			break;
		}

	}

	private void actionCamera() {
		Intent getImageByCamera = new Intent(
				"android.media.action.IMAGE_CAPTURE");
		startActivityForResult(getImageByCamera, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			Bitmap bm = null;
			try {
				Bundle extras = data.getExtras();
				bm = (Bitmap) extras.get("data");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bm != null) {
				switch (current_index) {
				case 1:
					Log.i("bm1:", bm.toString());
					bitmap_str_1 = bitmaptoString(bm);
					iv_1.setImageBitmap(bm);
					break;
				case 2:
					Log.i("bm2:", bm.toString());
					bitmap_str_2 = bitmaptoString(bm);
					iv_2.setImageBitmap(bm);
					break;
				case 3:
					Log.i("bm3:", bm.toString());
					bitmap_str_3 = bitmaptoString(bm);
					iv_3.setImageBitmap(bm);
					break;

				default:
					break;
				}

			}
		} else if (requestCode == 2) {
			Uri uri = data.getData();
			System.out.println("!!!!!!!!!!!!!!~~~uri");
			Log.e("uri", uri.toString());
			ContentResolver cr = this.getContentResolver();
			switch (current_index) {
			case 1:
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(cr
							.openInputStream(uri));
					ImageView imageView = (ImageView) findViewById(R.id.iv_1);
					/* 将Bitmap设定到ImageView */
					imageView.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(cr
							.openInputStream(uri));
					ImageView imageView = (ImageView) findViewById(R.id.iv_2);
					/* 将Bitmap设定到ImageView */
					imageView.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(cr
							.openInputStream(uri));
					ImageView imageView = (ImageView) findViewById(R.id.iv_3);
					/* 将Bitmap设定到ImageView */
					imageView.setImageBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}

		}
	}

	private void setImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			btn_crop.setBackgroundDrawable(drawable);
		}
	}

	public String bitmaptoString(Bitmap bitmap) {

		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;

	}

	public void onBankChange(int position) {
		bankbranchname = null;
		currentBank = parse_bank.getBanks().get(position);
	}

	class BankAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			onBankChange(position);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	public void onProvinChange(int position) {
		bankbranchname = null;
		currentProvince = parse.getProvinces().get(position);
		ArrayAdapter<CityModel> cityAdapter = new ArrayAdapter<CityModel>(this,
				R.layout.simple_spinner_item, android.R.id.text1,
				currentProvince.getCities());
		spinner2.setAdapter(cityAdapter);
	}

	class ProvinceAdapter implements OnItemSelectedListener {

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView,
		 *      android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			onProvinChange(position);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
		 */
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	final class CityAdapter extends ProvinceAdapter {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			bankbranchname = null;
			currentCity = currentProvince.getCities().get(position);
		}
	}

	public void phoneverifcode() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		try {
			Event event = new Event(null, "getSms", null);
			event.setTransfer("089006");
			String fsk = "Get_ExtPsamNo|null";
			event.setFsk(fsk);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mobNo", ApplicationEnvironment.getInstance()
					.getPreferences().getString(Constant.PHONENUM, ""));
			map.put("sendTime", date);
			map.put("type", "1");
			event.setStaticActivityDataMap(map);
			event.trigger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置图片")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent, 2);
							break;
						case 1:
							Intent getImageByCamera = new Intent(
									"android.media.action.IMAGE_CAPTURE");
							startActivityForResult(getImageByCamera, 1);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}
	// public boolean hasSdcard() {
	// return Environment.MEDIA_MOUNTED.equals(Environment
	// .getExternalStorageState());
	// }
}
