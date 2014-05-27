package com.bft.pos.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.bft.pos.dynamic.core.Event;
import com.bft.pos.model.CityModel;
import com.bft.pos.util.Bank;
import com.bft.pos.util.BankParse;
import com.bft.pos.util.JSONUtil;
import com.bft.pos.util.PopupMessageUtil;
import com.bft.pos.util.Province;
import com.bft.pos.util.ProvinceParse;

/**
 * 实名认证 上图图片
 */
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
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
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
	private EditText et_sms;

	private String merchant_name = null;
	private String mastername = null;

	private TextView tv_mastername = null;

	private String bankbranchid = null;
	private String bankbranchname = null;

	private Button btn_sms, bt_confirm;
	private Uri mOutPutFileUri;
	private Context context;
	private Bitmap bm = null;
	private Button btn_crop;
	String photo1 = null;
	/** 身份证正面 */
	String pIdImg0_path = "";
	/** 身份证反面 */
	String pIdImg1_path = "";
	String bkCardImg_path = "";
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "Acount.png";
	private String[] items = new String[] { "本地图片", "拍照" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.index = 0;
		// 添加了侧滑内容
		setLayoutIdsTest(R.layout.ws_munday_slidingmenu_test_menu,
				R.layout.activity_authentication_upimage);
		super.onCreate(savedInstanceState);

		Button btn_back = (Button) this.findViewById(R.id.backButton);
		btn_back.setOnClickListener(this);
		et_sms = (EditText) findViewById(R.id.et_sms);
		// btn_bank_branch = (Button) this.findViewById(R.id.btn_bank_branch);
		// btn_bank_branch.setOnClickListener(this);

		btn_sms = (Button) this.findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);

		bt_confirm = (Button) this.findViewById(R.id.bt_confirm);
		bt_confirm.setOnClickListener(this);
		buss_name = (EditText) findViewById(R.id.buss_name);
		et_account = (EditText) findViewById(R.id.et_account);
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
//		iv_3 = (ImageView) this.findViewById(R.id.iv_3);

		// iv_0.setOnClickListener(this);
		iv_1.setOnClickListener(this);
		iv_2.setOnClickListener(this);
//		iv_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.backButton:
			this.finish();
			break;
		case R.id.bt_confirm:
			System.out.println(pIdImg0_path + "~~~~" + pIdImg1_path + "!!~~"
					+ bkCardImg_path);
			try {
				Event event = new Event(null, "identifyMerchant", null);
				event.setTransfer("089020");
				String fsk = "Get_PsamNo|null";
				if (Constant.isAISHUA) {
					fsk = "getKsn|null";
				}
				event.setFsk(fsk);
				HashMap<String, String> map = new HashMap<String, String>();
				Map<String, Object> req_map = new HashMap<String, Object>();
				Map<String, String> tmp = new HashMap<String, String>();
				tmp.put("pIdImg0", pIdImg0_path);// 身份证正面
				tmp.put("pIdImg1", pIdImg1_path);// 身份证反面
				tmp.put("bkCardImg", bkCardImg_path);// 银行卡图片
				req_map.putAll(tmp);
				String req_json = JSONUtil.MAP2JSONStr(req_map);
				map.put("bankNo", "123456789");// 开户行
				map.put("bkCardNo", "1234567890");// 银行卡号
				map.put("mctName", "建行");// 商户名
				map.put("verifyCode", et_sms.getText().toString());// 验证码
				/** 附件内容 */
				map.put("attachments", req_json);
				event.setStaticActivityDataMap(map);
				event.trigger();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.iv_1:
			current_index = 1;
			// showDialog();
			actionCamera();
			break;
		case R.id.iv_2:
			current_index = 2;
			actionCamera();
			// showDialog();
			break;
//		case R.id.iv_3:
//			current_index = 3;
//			actionCamera();
			// showDialog();
			// actionCamera();
//			break;
		// 银行
		// case R.id.btn_bank_branch:
		// Intent intent = new Intent(AuthenticationUpImageActivity.this,
		// BankSearchActivity.class);
		// intent.putExtra("bankCode", currentBank.getCode());
		// intent.putExtra("provinceCode", currentCity.getProvince_code());
		// intent.putExtra("cityCode", currentCity.getCode());
		// AuthenticationUpImageActivity.this
		// .startActivityForResult(intent, 1);
		// break;
		// 短信验证
		case R.id.btn_sms:
			PopupMessageUtil.showMSG_middle2("短信已发送，请注意查收！");
			actionGetSms();
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
		if (requestCode == 1) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				return;
			}
			new DateFormat();
			String name = DateFormat.format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".jpg";
			Bundle bundle = data.getExtras();
			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			FileOutputStream b = null;
			File file = new File("/sdcard/myImage/abc");
			file.mkdirs();// 创建文件夹
			String fileName = file.getPath() + name;
			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			switch (current_index) {
			case 1:
				pIdImg0_path = fileName;
				iv_1.setImageBitmap(bitmap);// 将图片显示在ImageView里
				break;
			case 2:
				pIdImg1_path = fileName;

				iv_2.setImageBitmap(bitmap);
				break;
			case 3:
				bkCardImg_path = fileName;
				iv_3.setImageBitmap(bitmap);
				break;
			default:
				break;
			}

			// try {
			// Bundle extras = data.getExtras();
			// bm = (Bitmap) extras.get("data");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// if (bm != null) {
			//
			// switch (current_index) {
			// case 1:
			// String path = Environment.getExternalStorageDirectory()
			// .toString() + "/temp";
			// File path1 = new File(path);
			// if (!path1.exists()) {
			// path1.mkdirs();
			// }
			// File file = new File(path1, System.currentTimeMillis()
			// + ".jpg");
			// mOutPutFileUri = Uri.fromFile(file);
			// pIdImg0_path = Environment.getExternalStorageDirectory()
			// .toString()
			// + "/temp"
			// + System.currentTimeMillis()
			// + ".jpg";
			// System.out.println(pIdImg0_path
			// + "!!!!!!!!!~~~~1~~~~~~~~~~");
			// bitmap_str_1 = bitmaptoString(bm);
			// iv_1.setImageBitmap(bm);
			// break;
			// case 2:
			// String pathtwo = Environment.getExternalStorageDirectory()
			// .toString() + "/temp";
			// File path2 = new File(pathtwo);
			// if (!path2.exists()) {
			// path2.mkdirs();
			// }
			// File file2 = new File(path2, System.currentTimeMillis()
			// + ".jpg");
			// mOutPutFileUri = Uri.fromFile(file2);
			// pIdImg1_path = Environment.getExternalStorageDirectory()
			// .toString()
			// + "/temp"
			// + System.currentTimeMillis()
			// + ".jpg";
			// System.out.println(pIdImg1_path
			// + "!!!!!!!!!~~~~~~~2~~~~~~~");
			// bitmap_str_2 = bitmaptoString(bm);
			// iv_2.setImageBitmap(bm);
			// break;
			// case 3:
			// String pathth = Environment.getExternalStorageDirectory()
			// .toString() + "/temp";
			// File path3 = new File(pathth);
			// if (!path3.exists()) {
			// path3.mkdirs();
			// }
			// File file3 = new File(path3, System.currentTimeMillis()
			// + ".jpg");
			// mOutPutFileUri = Uri.fromFile(file3);
			// bkCardImg_path = Environment.getExternalStorageDirectory()
			// .toString()
			// + "/temp"
			// + System.currentTimeMillis()
			// + ".jpg";
			// System.out.println(bkCardImg_path
			// + "!!!!!!!!!~~~~~~~3~~~~~~~");
			// bitmap_str_3 = bitmaptoString(bm);
			// iv_3.setImageBitmap(bm);
			// break;
			//
			// default:
			// break;
			// }
			// }
		}
		Bitmap bm = null;
		// 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
		ContentResolver resolver = getContentResolver();
		if (requestCode == 2) {
			Uri originalUri = data.getData(); // 获得图片的uri
			try {
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				String[] proj = { MediaStore.Images.Media.DATA };
				// 好像是android多媒体数据库的封装接口，具体的看Android文档
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
				// 按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				String path = cursor.getString(column_index);
				startPhotoZoom(originalUri);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 显得到bitmap图片

			// try {
			// Uri selectedImage = data.getData();
			// String[] filePathColumn = { MediaStore.Images.Media.DATA };
			//
			// Cursor cursor = getContentResolver().query(selectedImage,
			// filePathColumn, null, null, null);
			// cursor.moveToFirst();
			//
			// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			// String picturePath = cursor.getString(columnIndex);
			// cursor.close();
			// iv_1.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			// } catch (Exception e) {
			// // TODO: handle exception
			// e.printStackTrace();
			// }
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			// Uri originalUri = data.getData(); // 获得图片的uri
			// try {
			// bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
			// String[] proj = { MediaStore.Images.Media.DATA };
			// // 好像是android多媒体数据库的封装接口，具体的看Android文档
			// Cursor cursor = managedQuery(originalUri, proj, null, null,
			// null);
			// // 按我个人理解 这个是获得用户选择的图片的索引值
			// int column_index = cursor
			// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// // 将光标移至开头 ，这个很重要，不小心很容易引起越界
			// cursor.moveToFirst();
			// // 最后根据索引值获取图片路径
			// String path = cursor.getString(column_index);
			// System.out.println(path + "~~~~~~~~~~~~~~~~~~~~~~~bd~~~~~~");
			// switch (current_index) {
			// case 1:
			// pIdImg0_path = path;
			// startPhotoZoom(originalUri);
			// break;
			// case 2:
			// pIdImg1_path = path;
			// startPhotoZoom(originalUri);
			// case 3:
			// bkCardImg_path = path;
			//
			// default:
			// break;
			// }
			//
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}

		// 处理结果
		if (requestCode == 3) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);// (0
				switch (current_index) {
				case 1:
					iv_1.setImageBitmap(photo);
					break;
				case 2:
					iv_2.setImageBitmap(photo);
					break;
				case 3:
					iv_3.setImageBitmap(photo);
					break;
				default:
					break;
				}

			}
		}

	}

	// case 1:
	// // 拍照
	// if (requestCode == PHOTOHRAPH) {
	// // 设置文件保存路径这里放在跟目录下
	// File picture1 = new File(
	// Environment.getExternalStorageDirectory() + "/temp.jpg");
	// pIdImg0_path = Environment.getExternalStorageDirectory()
	// + "/temp.jpg";
	// System.out.println("------------------------" + pIdImg0_path);
	// startPhotoZoom(Uri.fromFile(picture1));
	// }
	//
	// if (data == null)
	// return;
	//
	// // 读取相册缩放图片
	// if (requestCode == PHOTOZOOM) {
	// // ContentResolver resolver = getContentResolver();
	// // Uri originalUri = data.getData(); // 获得图片的uri
	// // File picture1 = new File(
	// // Environment.getExternalStorageDirectory() + "/temp.jpg");
	// // pIdImg0_path = Environment.getExternalStorageDirectory()
	// // + "/temp.jpg";
	// // System.out.println("---------xc---------------" +
	// // pIdImg0_path);
	// ContentResolver resolver = getContentResolver();
	// Uri originalUri = data.getData(); // 获得图片的uri
	// try {
	// bm = MediaStore.Images.Media.getBitmap(resolver,
	// originalUri);
	// String[] proj = { MediaStore.Images.Media.DATA };
	// // 好像是android多媒体数据库的封装接口，具体的看Android文档
	// Cursor cursor = managedQuery(originalUri, proj, null, null,
	// null);
	// // 按我个人理解 这个是获得用户选择的图片的索引值
	// int column_index = cursor
	// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// // 将光标移至开头 ，这个很重要，不小心很容易引起越界
	// cursor.moveToFirst();
	// // 最后根据索引值获取图片路径
	// String path = cursor.getString(column_index);
	// pIdImg0_path = path;
	// System.out.println("---------xc---------------"
	// + pIdImg0_path);
	// // startPhotoZoom(originalUri);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// // 处理结果
	// if (requestCode == PHOTORESOULT) {
	// Bundle extras = data.getExtras();
	// if (extras != null) {
	// Bitmap photo = extras.getParcelable("data");
	// ByteArrayOutputStream stream = new ByteArrayOutputStream();
	// photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0
	// // -
	// iv_1.setImageBitmap(photo);
	// }
	// }
	// break;
	// case 2:
	// // 拍照
	// if (requestCode == PHOTOHRAPH) {
	// // 设置文件保存路径这里放在跟目录下
	// File picture2 = new File(
	// Environment.getExternalStorageDirectory() + "/temp.jpg");
	// pIdImg1_path = Environment.getExternalStorageDirectory()
	// + "/temp2.jpg";
	// System.out.println("------------------------"
	// + picture2.getPath());
	// startPhotoZoom(Uri.fromFile(picture2));
	// }
	//
	// if (data == null)
	// return;
	//
	// // 读取相册缩放图片
	// if (requestCode == PHOTOZOOM) {
	// ContentResolver resolver = getContentResolver();
	// Uri originalUri2 = data.getData(); // 获得图片的uri
	// File picture2 = new File(
	// Environment.getExternalStorageDirectory()
	// + "/temp2.jpg");
	// pIdImg1_path = Environment.getExternalStorageDirectory()
	// + "/temp2.jpg";
	// System.out.println("------------xc------------"
	// + picture2.getPath());
	// startPhotoZoom(originalUri2);
	// }
	// // 处理结果
	// if (requestCode == PHOTORESOULT) {
	// Bundle extras = data.getExtras();
	// if (extras != null) {
	// Bitmap photo = extras.getParcelable("data");
	// ByteArrayOutputStream stream = new ByteArrayOutputStream();
	// photo.compress(CompressFormat.PNG, 100, stream);
	// iv_2.setImageBitmap(photo);
	// }
	// }
	// break;
	// case 3:
	// // 拍照
	// if (requestCode == PHOTOHRAPH) {
	// // 设置文件保存路径这里放在跟目录下
	// File picture3 = new File(
	// Environment.getExternalStorageDirectory() + "/temp.jpg");
	// bkCardImg_path = Environment.getExternalStorageDirectory()
	// + "/temp.jpg";
	// System.out.println("------------------------"
	// + picture3.getPath());
	// startPhotoZoom(Uri.fromFile(picture3));
	// }
	//
	// if (data == null)
	// return;
	//
	// // 读取相册缩放图片
	// if (requestCode == PHOTOZOOM) {
	// ContentResolver resolver = getContentResolver();
	// Uri originalUri3 = data.getData(); // 获得图片的uri
	// File picture3 = new File(
	// Environment.getExternalStorageDirectory()
	// + "/temp3.jpg");
	// bkCardImg_path = Environment.getExternalStorageDirectory()
	// + "/temp3.jpg";
	// System.out.println("------------------------"
	// + picture3.getPath());
	// startPhotoZoom(originalUri3);
	// }
	// // 处理结果
	// if (requestCode == PHOTORESOULT) {
	// Bundle extras = data.getExtras();
	// if (extras != null) {
	// Bitmap photo = extras.getParcelable("data");
	// ByteArrayOutputStream stream = new ByteArrayOutputStream();
	// photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0
	// // -
	// iv_3.setImageBitmap(photo);
	// }
	// }
	// break;
	// default:
	// break;
	// }

	// }

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

	// public void phoneverifcode() {
	// SimpleDateFormat sDateFormat = new SimpleDateFormat(
	// "yyyy-MM-dd hh:mm:ss");
	// String date = sDateFormat.format(new java.util.Date());
	// try {
	// Event event = new Event(null, "getSms", null);
	// event.setTransfer("089006");
	// String fsk = "Get_ExtPsamNo|null";
	// event.setFsk(fsk);
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put("mobNo", ApplicationEnvironment.getInstance()
	// .getPreferences().getString(Constant.PHONENUM, ""));
	// map.put("sendTime", date);
	// map.put("type", "1");
	// event.setStaticActivityDataMap(map);
	// event.trigger();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置图片")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							return;
							// 本地
							// Intent intent = new Intent();
							// /* 开启Pictures画面Type设定为image */
							// intent.setType("image/*");
							// /* 使用Intent.ACTION_GET_CONTENT这个Action */
							// intent.setAction(Intent.ACTION_GET_CONTENT);
							// /* 取得相片后返回本画面 */
							// startActivityForResult(intent, 2);
							// break;
						case 1:
							Intent getImageByCamera = new Intent(
									"android.media.action.IMAGE_CAPTURE");

							startActivityForResult(getImageByCamera, 1);
							// 拍照
							// Intent intent1 = new Intent(
							// MediaStore.ACTION_IMAGE_CAPTURE);
							// intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							// .fromFile(new File(Environment
							// .getExternalStorageDirectory(),
							// "temp.jpg")));
							// System.out.println("============="
							// + Environment.getExternalStorageDirectory());
							// // photo1 =
							// // Environment.getExternalStorageDirectory()
							// // .toString() + "/test";
							// // File path1 = new File(photo1);
							// // if (!path1.exists()) {
							// // path1.mkdirs();
							// // }
							// // File file = new File(path1, System
							// // .currentTimeMillis() + ".png");
							// //
							// // intent1.putExtra(MediaStore.EXTRA_OUTPUT,
							// // Uri.fromFile(file));
							// System.out.println("============="
							// + Environment.getExternalStorageDirectory());
							// startActivityForResult(intent1, 1);
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

	public String getPhotoPath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = managedQuery(uri, proj, null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路
		String path = cursor.getString(column_index);
		return path;
	}

	public String saveImage(Bitmap photo, String spath) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(spath, false));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spath;
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 64);
		intent.putExtra("outputY", 64);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 获取验证码
	 */
	@SuppressLint("SimpleDateFormat")
	private void actionGetSms() {
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

	/** 为图片创建不同的名称用于保存，避免覆盖 **/

	public static String createFileName() {

		String fileName = "";

		Date date = new Date(System.currentTimeMillis()); // 系统当前时间

		SimpleDateFormat dateFormat = new SimpleDateFormat(

		"'IMG'_yyyyMMdd_HHmmss");

		fileName = dateFormat.format(date) + ".jpg";

		return fileName;

	}

}
