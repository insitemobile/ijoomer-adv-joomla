package com.ijoomer.src;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.ijoomer.common.classes.IjoomerRegistrationMaster;
import com.ijoomer.common.classes.IjoomerUtilities;
import com.ijoomer.custom.interfaces.CustomClickListner;
import com.ijoomer.customviews.IjoomerButton;
import com.ijoomer.customviews.IjoomerEditText;
import com.ijoomer.customviews.IjoomerTextView;
import com.ijoomer.map.IjoomerMapAddress;
import com.ijoomer.oauth.IjoomerRegistration;
import com.ijoomer.weservice.IjoomerWebService;
import com.ijoomer.weservice.WebCallListener;
import com.smart.framework.CustomAlertNeutral;

public class IjoomerRegistrationStep2Activity extends IjoomerRegistrationMaster {

	private LinearLayout lnr_form;
	private IjoomerEditText editMap;
	private IjoomerButton btnBack;
	private IjoomerButton btnSubmit;

	ArrayList<HashMap<String, String>> fields;
	ArrayList<HashMap<String, String>> groups;
	final private int GET_ADDRESS_FROM_MAP = 1;

	/**
	 * Overrides method
	 */

	@Override
	public int setLayoutId() {
		return R.layout.ijoomer_registration_step2;
	}

	@Override
	public void initComponents() {
		lnr_form = (LinearLayout) findViewById(R.id.lnr_form);
		btnBack = (IjoomerButton) findViewById(R.id.btnBack);
		btnSubmit = (IjoomerButton) findViewById(R.id.btnSubmit);
		createForm();
		setEditable(true);
	}

	@Override
	public void prepareViews() {
		((IjoomerTextView) getHeaderView().findViewById(R.id.txtHeader)).setText(getString(R.string.header_registration));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == GET_ADDRESS_FROM_MAP) {
				editMap.setText(((HashMap<String, String>) data.getSerializableExtra("MAP_ADDRESSS_DATA")).get("address"));
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		}

	}

	@Override
	public void setActionListeners() {
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideSoftKeyboard();
				submitNewUser();

			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Class method
	 */

	@SuppressWarnings("unchecked")
	private void setEditable(boolean isEditable) {
		int size = lnr_form.getChildCount();
		if (isEditable) {
			for (int i = 0; i < size; i++) {

				View v = lnr_form.getChildAt(i);
				HashMap<String, String> row = (HashMap<String, String>) v.getTag();
				if (((LinearLayout) v.findViewById(R.id.lnrGgroup)).getVisibility() == View.VISIBLE) {

				} else {
					if (row.get("type").equals("text")) {
						((LinearLayout) v.findViewById(R.id.lnrEdit)).setVisibility(View.VISIBLE);
					} else if (row.get("type").equals("textarea")) {
						((LinearLayout) v.findViewById(R.id.lnrEditArea)).setVisibility(View.VISIBLE);
					} else if (row.get("type").equals("date")) {
						((LinearLayout) v.findViewById(R.id.lnrEditClickable)).setVisibility(View.VISIBLE);
					} else if (row.get("type").equals("select")) {
						((LinearLayout) v.findViewById(R.id.lnrSpin)).setVisibility(View.VISIBLE);
					}

					((LinearLayout) v.findViewById(R.id.lnrReadOnly)).setVisibility(View.GONE);
				}

			}
		} else {
			for (int i = 0; i < size; i++) {

				View v = lnr_form.getChildAt(i);
				HashMap<String, String> row = (HashMap<String, String>) v.getTag();
				if (((LinearLayout) v.findViewById(R.id.lnrGgroup)).getVisibility() == View.VISIBLE) {

				} else {
					if (row.get("type").equals("text")) {
						((LinearLayout) v.findViewById(R.id.lnrEdit)).setVisibility(View.GONE);
					} else if (row.get("type").equals("textarea")) {
						((LinearLayout) v.findViewById(R.id.lnrEditArea)).setVisibility(View.GONE);
					} else if (row.get("type").equals("date")) {
						((LinearLayout) v.findViewById(R.id.lnrEditClickable)).setVisibility(View.GONE);
					} else if (row.get("type").equals("select")) {
						((LinearLayout) v.findViewById(R.id.lnrSpin)).setVisibility(View.GONE);
					}

					((LinearLayout) v.findViewById(R.id.lnrReadOnly)).setVisibility(View.VISIBLE);
				}

			}
		}

	}

	private void submitNewUser() {
		boolean validationFlag = true;
		ArrayList<HashMap<String, String>> signUpFields = new ArrayList<HashMap<String, String>>();
		int size = lnr_form.getChildCount();
		for (int i = 0; i < size; i++) {
			LinearLayout v = (LinearLayout) lnr_form.getChildAt(i);
			@SuppressWarnings("unchecked")
			HashMap<String, String> field = (HashMap<String, String>) v.getTag();

			IjoomerEditText edtValue = null;
			Spinner spnrValue = null;
			ImageView imgPrivacyValue = null;

			if (field != null) {
				if (field.get("type").equals("text")) {
					edtValue = (IjoomerEditText) ((LinearLayout) v.findViewById(R.id.lnrEdit)).findViewById(R.id.txtValue);
					imgPrivacyValue = (ImageView) ((LinearLayout) v.findViewById(R.id.lnrEdit)).findViewById(R.id.imgPrivacyValue);
				} else if (field.get("type").equals("textarea")) {
					edtValue = (IjoomerEditText) ((LinearLayout) v.findViewById(R.id.lnrEditArea)).findViewById(R.id.txtValue);
					imgPrivacyValue = (ImageView) ((LinearLayout) v.findViewById(R.id.lnrEditArea)).findViewById(R.id.imgPrivacyValue);
				} else if (field.get("type").equals("map")) {
					edtValue = (IjoomerEditText) ((LinearLayout) v.findViewById(R.id.lnrEditMap)).findViewById(R.id.txtValue);
					imgPrivacyValue = (ImageView) ((LinearLayout) v.findViewById(R.id.lnrEditMap)).findViewById(R.id.imgPrivacyValue);
				} else if (field.get("type").equals("date")) {
					edtValue = (IjoomerEditText) ((LinearLayout) v.findViewById(R.id.lnrEditClickable)).findViewById(R.id.txtValue);
					imgPrivacyValue = (ImageView) ((LinearLayout) v.findViewById(R.id.lnrEditClickable)).findViewById(R.id.imgPrivacyValue);

					if (edtValue.getText().toString().trim().length() > 0) {
						if (!IjoomerUtilities.birthdateValidator(edtValue.getText().toString().trim())) {
							edtValue.setFocusable(true);
							edtValue.setError(getString(R.string.validation_invalid_birth_date));
							validationFlag = false;
						}
					}
				}

				if (field.get("type").equals("select")) {
					spnrValue = (Spinner) ((LinearLayout) v.findViewById(R.id.lnrSpin)).findViewById(R.id.txtValue);
					imgPrivacyValue = (ImageView) ((LinearLayout) v.findViewById(R.id.lnrSpin)).findViewById(R.id.imgPrivacyValue);
					field.put("value", spnrValue.getSelectedItem().toString());
					field.put("privacy", imgPrivacyValue.getTag().toString());
					signUpFields.add(field);
				} else if (edtValue != null && edtValue.getText().toString().trim().length() <= 0 && (field.get("required").equals("1"))) {
					edtValue.setError(getString(R.string.validation_value_required));
					validationFlag = false;
				} else {
					field.put("value", edtValue.getText().toString().trim());
					field.put("privacy", imgPrivacyValue.getTag().toString());
					signUpFields.add(field);
				}
			}
		}

		if (validationFlag) {
			final SeekBar proSeekBar = IjoomerUtilities.getLoadingDialog(getString(R.string.dialog_loading_register_newuser));
			new IjoomerRegistration(this).submitNewUser(signUpFields, new WebCallListener() {

				@Override
				public void onProgressUpdate(int progressCount) {
					proSeekBar.setProgress(progressCount);
				}

				@Override
				public void onCallComplete(final int responseCode, String errorMessage, ArrayList<HashMap<String, String>> data1, Object data2) {
					if (responseCode == 200) {
						IjoomerUtilities.getCustomOkDialog(getString(R.string.dialog_loading_profile), getString(R.string.registration_successfully), getString(R.string.ok),
								R.layout.ijoomer_ok_dialog, new CustomAlertNeutral() {

									@Override
									public void NeutralMathod() {

										Intent intent = new Intent("clearStackActivity");
										intent.setType("text/plain");
										sendBroadcast(intent);
										IjoomerWebService.cookies = null;

										loadNew(IjoomerLoginActivity.class, IjoomerRegistrationStep2Activity.this, true);
										finish();
									}
								});

					} else {
						IjoomerUtilities.getCustomOkDialog(getString(R.string.dialog_loading_profile),
								getString(getResources().getIdentifier("code" + responseCode, "string", getPackageName())), getString(R.string.ok), R.layout.ijoomer_ok_dialog,
								new CustomAlertNeutral() {

									@Override
									public void NeutralMathod() {
										if (responseCode == 599) {
											finish();
										}
									}
								});
					}
				}
			});
		}
	}

	private void createForm() {
		groups = new IjoomerRegistration(IjoomerRegistrationStep2Activity.this).getFieldGroups();
		LayoutInflater inflater = LayoutInflater.from(IjoomerRegistrationStep2Activity.this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.FILL_PARENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;

		int size = groups.size();
		for (int i = 0; i < size; i++) {
			View groupView = inflater.inflate(R.layout.ijoomer_registration_dynamic_view_item, null);
			((LinearLayout) groupView.findViewById(R.id.lnrGgroup)).setVisibility(View.VISIBLE);
			((IjoomerTextView) groupView.findViewById(R.id.txtLable)).setText(groups.get(i).get("group_name"));
			lnr_form.addView(groupView, params);

			fields = new IjoomerRegistration(IjoomerRegistrationStep2Activity.this).getFields(groups.get(i).get("group_name"));
			LinearLayout layout = null;
			int len = fields.size();
			for (int j = 0; j < len; j++) {
				final HashMap<String, String> field = fields.get(j);
				View fieldView = inflater.inflate(R.layout.ijoomer_registration_dynamic_view_item, null);

				final Spinner spnWhoCanSee;
				final ImageView imgPrivacyValue;
				final ImageView imgPrivacyValueReadOnly;
				if (field.get("type").equals("text")) {
					final IjoomerEditText edit;
					layout = ((LinearLayout) fieldView.findViewById(R.id.lnrEdit));
					layout.setVisibility(View.VISIBLE);
					edit = ((IjoomerEditText) layout.findViewById(R.id.txtValue));
					if (field.get("value").toString().trim().length() > 0) {
						edit.setText(field.get("value"));
					} else {
						edit.setText(field.get("value"));
					}
					if (field.get("caption").contains(getString(R.string.phone)) || field.get("caption").contains(getString(R.string.year))) {
						edit.setInputType(InputType.TYPE_CLASS_NUMBER);
					} else if (field.get("caption").contains(getString(R.string.website)) || field.get("caption").contains(getString(R.string.email))) {
						edit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
					}
				} else if (field.get("type").equals("textarea")) {
					final IjoomerEditText edit;
					layout = ((LinearLayout) fieldView.findViewById(R.id.lnrEditArea));
					layout.setVisibility(View.VISIBLE);
					edit = ((IjoomerEditText) layout.findViewById(R.id.txtValue));

					if (field.get("value").toString().trim().length() > 0) {
						edit.setText(field.get("value"));
					} else {
					}

				} else if (field.get("type").equals("map")) {
					final IjoomerEditText edit;
					final ImageView imgMap;
					layout = ((LinearLayout) fieldView.findViewById(R.id.lnrEditMap));
					layout.setVisibility(View.VISIBLE);
					edit = ((IjoomerEditText) layout.findViewById(R.id.txtValue));
					imgMap = ((ImageView) layout.findViewById(R.id.imgMap));
					if (field.get("value").toString().trim().length() > 0) {
						edit.setText(field.get("value"));
					} else {
						if (field.get("caption").equalsIgnoreCase(getString(R.string.state))) {
							try {
								Address address = IjoomerUtilities.getAddressFromLatLong(0, 0);
								edit.setText(address.getAdminArea().replace(address.getCountryName() == null ? "" : address.getCountryName(), "")
										.replace(address.getPostalCode() == null ? "" : address.getPostalCode(), ""));
							} catch (Throwable e) {
								edit.setText("");
							}
						} else if (field.get("caption").equalsIgnoreCase(getString(R.string.city_town))) {
							try {
								Address address = IjoomerUtilities.getAddressFromLatLong(0, 0);
								edit.setText(address.getSubAdminArea());
							} catch (Throwable e) {
								edit.setText("");
							}
						} else {
							edit.setText(field.get("value"));
						}
					}

					imgMap.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							editMap = edit;
							Intent intent = new Intent(IjoomerRegistrationStep2Activity.this, IjoomerMapAddress.class);
							startActivityForResult(intent, GET_ADDRESS_FROM_MAP);
						}
					});

				} else if (field.get("type").equals("select")) {
					layout = ((LinearLayout) fieldView.findViewById(R.id.lnrSpin));
					layout.setVisibility(View.VISIBLE);
					final Spinner spn;
					spn = ((Spinner) layout.findViewById(R.id.txtValue));
					spn.setAdapter(IjoomerUtilities.getSpinnerAdapter(field));
					if (field.get("caption").equalsIgnoreCase(getString(R.string.country))) {

						try {
							Address address = IjoomerUtilities.getAddressFromLatLong(0, 0);
							String country = address.getCountryName();
							int selectedIndex = 0;
							JSONArray jsonArray = null;

							jsonArray = new JSONArray(field.get("options"));
							int optionSize = jsonArray.length();
							for (int k = 0; k < optionSize; k++) {
								JSONObject options = (JSONObject) jsonArray.get(k);

								if (options.getString("value").equalsIgnoreCase(country)) {
									selectedIndex = k;
									break;
								}
							}
							spn.setSelection(selectedIndex);
						} catch (Throwable e) {
							e.printStackTrace();
							spn.setSelection(0);
						}

					}

				} else if (field.get("type").equals("date")) {
					final IjoomerEditText edit;
					layout = ((LinearLayout) fieldView.findViewById(R.id.lnrEditClickable));
					layout.setVisibility(View.VISIBLE);
					edit = ((IjoomerEditText) layout.findViewById(R.id.txtValue));
					edit.setText(field.get("value"));
					edit.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							IjoomerUtilities.getDateDialog(((IjoomerEditText) v).getText().toString(), true, new CustomClickListner() {

								@Override
								public void onClick(String value) {
									((IjoomerEditText) v).setText(value);
								}
							});

						}
					});

				} else if (field.get("type").equals("selectmulti")) {
					final IjoomerEditText edit;
					layout = ((LinearLayout) fieldView.findViewById(R.id.lnrEditClickable));
					layout.setVisibility(View.VISIBLE);
					edit = ((IjoomerEditText) layout.findViewById(R.id.txtValue));
					edit.setText(field.get("value"));
					edit.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							IjoomerUtilities.getMultiSelectionDialog(field.get("caption"), field.get("options"), "", new CustomClickListner() {

								@Override
								public void onClick(String value) {
									((IjoomerEditText) v).setText(value);
								}
							});

						}
					});
				}

				if (field.get("required").equalsIgnoreCase("1")) {
					((IjoomerTextView) layout.findViewById(R.id.txtLable)).setText(field.get("caption") + " *");
				} else {
					((IjoomerTextView) layout.findViewById(R.id.txtLable)).setText(field.get("caption"));
				}

				imgPrivacyValue = ((ImageView) layout.findViewById(R.id.imgPrivacyValue));
				spnWhoCanSee = ((Spinner) layout.findViewById(R.id.spnWhoCanSee));
				spnWhoCanSee.setAdapter(IjoomerUtilities.getPrivacySpinnerAdapter(field));

				spnWhoCanSee.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {

						try {
							imgPrivacyValue.setTag(((JSONObject) new JSONObject(field.get("privacy")).getJSONArray("options").get(pos)).getString("value"));
						} catch (Throwable e) {
							e.printStackTrace();
						}
						if (imgPrivacyValue.getTag().toString().equals("0")) {
							imgPrivacyValue.setImageResource(R.drawable.jom_privacy_public);
						} else if (imgPrivacyValue.getTag().toString().equals("20")) {
							imgPrivacyValue.setImageResource(R.drawable.jom_privacy_sitemember);
						} else if (imgPrivacyValue.getTag().toString().equals("30")) {
							imgPrivacyValue.setImageResource(R.drawable.jom_privacy_friend);
						} else if (imgPrivacyValue.getTag().toString().equals("40")) {
							imgPrivacyValue.setImageResource(R.drawable.jom_privacy_onlyme);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});

				try {
					if (new JSONObject(field.get("privacy")).getString("value").equals("0")) {
						imgPrivacyValue.setImageResource(R.drawable.jom_privacy_public);
					} else if (new JSONObject(field.get("privacy")).getString("value").equals("20")) {
						imgPrivacyValue.setImageResource(R.drawable.jom_privacy_sitemember);
					} else if (new JSONObject(field.get("privacy")).getString("value").equals("30")) {
						imgPrivacyValue.setImageResource(R.drawable.jom_privacy_friend);
					} else if (new JSONObject(field.get("privacy")).getString("value").equals("40")) {
						imgPrivacyValue.setImageResource(R.drawable.jom_privacy_onlyme);
					}
					imgPrivacyValue.setTag(new JSONObject(field.get("privacy")).getString("value"));
					int privacySize = new JSONObject(field.get("privacy")).getJSONArray("options").length();
					for (int k = 0; k < privacySize; k++) {
						if (((JSONObject) new JSONObject(field.get("privacy")).getJSONArray("options").get(k)).getString("value").equals(
								new JSONObject(field.get("privacy")).getString("value"))) {
							spnWhoCanSee.setSelection(k);
							break;
						}
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				imgPrivacyValue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						spnWhoCanSee.performClick();
					}
				});

				((LinearLayout) fieldView.findViewById(R.id.lnrEdit)).setVisibility(View.GONE);
				((LinearLayout) fieldView.findViewById(R.id.lnrEditArea)).setVisibility(View.GONE);
				((LinearLayout) fieldView.findViewById(R.id.lnrSpin)).setVisibility(View.GONE);
				((LinearLayout) fieldView.findViewById(R.id.lnrEditClickable)).setVisibility(View.GONE);

				layout = ((LinearLayout) fieldView.findViewById(R.id.lnrReadOnly));
				layout.setVisibility(View.VISIBLE);

				((IjoomerTextView) layout.findViewById(R.id.txtLable)).setText(field.get("caption"));
				imgPrivacyValueReadOnly = (ImageView) layout.findViewById(R.id.imgPrivacyValue);
				try {
					if (new JSONObject(field.get("privacy")).getString("value").equals("0")) {
						imgPrivacyValueReadOnly.setImageResource(R.drawable.jom_privacy_public);
					} else if (new JSONObject(field.get("privacy")).getString("value").equals("20")) {
						imgPrivacyValueReadOnly.setImageResource(R.drawable.jom_privacy_sitemember);
					} else if (new JSONObject(field.get("privacy")).getString("value").equals("30")) {
						imgPrivacyValueReadOnly.setImageResource(R.drawable.jom_privacy_friend);
					} else if (new JSONObject(field.get("privacy")).getString("value").equals("40")) {
						imgPrivacyValueReadOnly.setImageResource(R.drawable.jom_privacy_onlyme);
					}
					imgPrivacyValueReadOnly.setTag(new JSONObject(field.get("privacy")).getString("value"));
				} catch (Throwable e) {
					e.printStackTrace();
				}
				((IjoomerTextView) layout.findViewById(R.id.txtValue)).setText(field.get("value"));
				fieldView.setTag(field);
				lnr_form.addView(fieldView, params);
			}

		}
	}

}