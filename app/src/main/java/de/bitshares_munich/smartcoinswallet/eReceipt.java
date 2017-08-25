package de.bitshares_munich.smartcoinswallet;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.bitshares_munich.database.HistoricalTransferEntry;
import de.bitshares_munich.database.SCWallDatabase;
import de.bitshares_munich.interfaces.GravatarDelegate;
import de.bitshares_munich.interfaces.IBalancesDelegate;
import de.bitshares_munich.models.AccountDetails;
import de.bitshares_munich.models.EquivalentFiatStorage;
import de.bitshares_munich.models.Gravatar;
import de.bitshares_munich.models.MerchantEmail;
import de.bitshares_munich.utils.Application;
import de.bitshares_munich.utils.Helper;
import de.bitshares_munich.utils.SupportMethods;
import de.bitshares_munich.utils.TableViewClickListener;
import de.bitshares_munich.utils.TinyDB;
import de.bitsharesmunich.graphenej.UserAccount;
import de.bitsharesmunich.graphenej.Util;
import de.bitsharesmunich.graphenej.models.HistoricalTransfer;
import de.bitsharesmunich.graphenej.operations.TransferOperation;

/**
 * Created by Syed Muhammad Muzzammil on 5/26/16.
 */
public class eReceipt extends BaseActivity implements IBalancesDelegate, GravatarDelegate {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public final String TAG = this.getClass().getName();

    @Bind(R.id.ivOtherGravatar)
    ImageView ivOtherGravatar;

    @Bind(R.id.tvOtherCompany)
    TextView tvOtherCompany;

    @Bind(R.id.tvTime)
    TextView tvTime;

    @Bind(R.id.tvOtherName)
    TextView tvOtherName;

    @Bind(R.id.tvUserName)
    TextView tvUserName;

    @Bind(R.id.tvUserId)
    TextView tvUserId;

    @Bind(R.id.memo)
    TextView memo;

    @Bind(R.id.tvAmount)
    TextView tvAmount;

    @Bind(R.id.tvAddress)
    TextView tvAddress;

    @Bind(R.id.tvAmountEquivalent)
    TextView tvAmountEquivalent;

    @Bind(R.id.tvBlockNumber)
    TextView tvBlockNumber;

    @Bind(R.id.tvTrxInBlock)
    TextView tvTrxInBlock;

    @Bind(R.id.tvFee)
    TextView tvFee;

    @Bind(R.id.tvFeeEquivalent)
    TextView tvFeeEquivalent;

    @Bind(R.id.tvPaymentAmount)
    TextView tvPaymentAmount;

    @Bind(R.id.tvPaymentEquivalent)
    TextView tvPaymentEquivalent;

    @Bind(R.id.tvTotalEquivalent)
    TextView tvTotalEquivalent;

    @Bind(R.id.tvTotal)
    TextView tvTotal;

    @Bind(R.id.tvOtherStatus)
    TextView tvOtherStatus;

    @Bind(R.id.tvUserStatus)
    TextView tvUserStatus;

    @Bind(R.id.ivImageTag)
    ImageView ivImageTag;

    @Bind(R.id.buttonSend)
    ImageButton buttonSend;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.llall)
    LinearLayout llall;

    int assets_id_in_work;
    int assets_id_total_size;
    HashMap<String, String> Freemap = new HashMap<>();
    HashMap<String, String> Amountmap = new HashMap<>();
    List<String> Assetid = new ArrayList<>();
    HashMap<String, HashMap<String, String>> SymbolsPrecisions = new HashMap<>();
    String memoMsg;
    String date;
    String otherName;
    String userName;
    String feeSymbol = "";
    String amountSymbol = "";
    String feeAmount = "";
    String amountAmount = "";
    String time = "";
    String timeZone = "";
    ProgressDialog progressDialog;
    boolean loadComplete = false;
    boolean btnPress = false;

    /* Transaction id */
    String transactionId = "";

    /* Reference to the class containing all blockchain details about this transaction */
    private HistoricalTransferEntry historicalTransferEntry;

    /* Legacy persistent storage */
    private TinyDB tinyDB;

    /* Database interface reference */
    private SCWallDatabase database;

    /* Current user */
    private UserAccount user;

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 90;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_receipt);
        ButterKnife.bind(this);

        // Instantiating the database
        database = new SCWallDatabase(this);

        // Retrieving the currently active account from the legacy tinyDB implementation
        tinyDB = new TinyDB(this);
        ArrayList<AccountDetails> accountDetails = tinyDB.getListObject(getString(R.string.pref_wallet_accounts), AccountDetails.class);
        for (AccountDetails accountDetail : accountDetails) {
            if (accountDetail.isSelected) {
                user = database.fillUserDetails(new UserAccount(accountDetail.account_id));
            }
        }

        // Deserializing the HistoricalTransferEntry object, which contains all
        // detailed information about this transfer.
        Gson gson = new Gson();
        String jsonOperation = getIntent().getExtras().getString(TableViewClickListener.KEY_OPERATION_ENTRY);
        historicalTransferEntry = gson.fromJson(jsonOperation, HistoricalTransferEntry.class);

        // Setting the memo message
        TransferOperation transfer = historicalTransferEntry.getHistoricalTransfer().getOperation();
        memo.setText(String.format(memo.getText().toString(), transfer.getMemo().getPlaintextMessage()));

        transactionId = historicalTransferEntry.getHistoricalTransfer().getId();

        progressDialog = new ProgressDialog(this);
        Application.registerBalancesDelegateEReceipt(this);
        setTitle(getResources().getString(R.string.e_receipt_activity_name));
        hideProgressBar();
        Intent intent = getIntent();
        String eReciept = intent.getStringExtra(getResources().getString(R.string.e_receipt));

        memoMsg = intent.getStringExtra("Memo");
        date = intent.getStringExtra("Date");
        time = intent.getStringExtra("Time");
        timeZone = intent.getStringExtra("TimeZone");

        UserAccount fromUser = historicalTransferEntry.getHistoricalTransfer().getOperation().getFrom();
        UserAccount toUser = historicalTransferEntry.getHistoricalTransfer().getOperation().getTo();
        if (fromUser.getObjectId().equals(user.getObjectId())) {
            ivImageTag.setImageResource(R.drawable.send);
            tvUserStatus.setText(getString(R.string.sender_account));
            tvOtherStatus.setText(getString(R.string.receiver_account));
            otherName = toUser.getName();
            userName = fromUser.getName();
        } else {
            tvUserStatus.setText(getString(R.string.receiver_account));
            tvOtherStatus.setText(getString(R.string.sender_account));
            ivImageTag.setImageResource(R.drawable.receive);
            otherName = fromUser.getName();
            userName = toUser.getName();
        }

        tvOtherName.setText(otherName);
        tvUserName.setText(userName);
//        TvBlockNum.setText(date);
        tvTime.setText(time + " " + timeZone);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ivOtherGravatar.requestLayout();

        ivOtherGravatar.getLayoutParams().height = (width * 40) / 100;
        ivOtherGravatar.getLayoutParams().width = (width * 40) / 100;


        fetchGravatarInfo(get_email(otherName));

        setBackButton(true);

        HistoricalTransfer historicalTransfer = historicalTransferEntry.getHistoricalTransfer();
        tvBlockNumber.setText(String.format("%d", historicalTransfer.getBlockNum()));
        tvTrxInBlock.setText(String.format("%s", historicalTransfer.getId()));

        double amount = Util.fromBase(historicalTransfer.getOperation().getAssetAmount());
        String symbol = historicalTransfer.getOperation().getAssetAmount().getAsset().getSymbol();
        int precision = historicalTransfer.getOperation().getAssetAmount().getAsset().getPrecision();
        String textFormat = String.format("%%.%df %%s", precision);
        tvPaymentAmount.setText(String.format(textFormat, amount, symbol));

        if (historicalTransferEntry.getEquivalentValue() != null) {
            double eqValueAmount = Util.fromBase(historicalTransferEntry.getEquivalentValue());
            String eqValueSymbol = historicalTransferEntry.getEquivalentValue().getAsset().getSymbol();
            tvPaymentEquivalent.setText(String.format("%.2f %s", eqValueAmount, eqValueSymbol));
        }
    }

    @Override
    public void OnUpdate(String s, int id) {
        if (id == 18) {
            assets_id_in_work = 0;
            get_asset(Assetid.get(assets_id_in_work), "19");
        } else if (id == 19) {
            if (assets_id_in_work < assets_id_total_size) {
                String result = SupportMethods.ParseJsonObject(s, "result");
                String assetObject = SupportMethods.ParseObjectFromJsonArray(result, 0);
                String symbol = SupportMethods.ParseJsonObject(assetObject, "symbol");
                String precision = SupportMethods.ParseJsonObject(assetObject, "precision");
                HashMap<String, String> de = new HashMap<>();
                de.put("symbol", symbol);
                de.put("precision", precision);
                SymbolsPrecisions.put(Assetid.get(assets_id_in_work), de);
                if (assets_id_in_work == (assets_id_total_size - 1)) {
                    onLastCall();
                }
                assets_id_in_work++;
                if (assets_id_in_work < Assetid.size())
                    get_asset(Assetid.get(assets_id_in_work), "19");
            }
        }
    }

    void onLastCall() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                AssetsSymbols assetsSymbols = new AssetsSymbols(eReceipt.this);

                HashMap<String, String> sym_preFee = SymbolsPrecisions.get(Freemap.get("asset_id"));
//                feeAmount = SupportMethods.ConvertValueintoPrecision(sym_preFee.get("precision"), Freemap.get("amount"));
                //TODO: Fix this!
                feeAmount = "0";
                feeSymbol = sym_preFee.get("symbol");

                HashMap<String, String> sym_preAmount = SymbolsPrecisions.get(Amountmap.get("asset_id"));
                //TODO: Fix this
//                amountAmount = SupportMethods.ConvertValueintoPrecision(sym_preAmount.get("precision"), Amountmap.get("amount"));
                amountAmount = "0.0";
                amountSymbol = sym_preAmount.get("symbol");


                EquivalentComponents equivalentAmount = new EquivalentComponents();
                equivalentAmount.Amount = Float.parseFloat(amountAmount);
                equivalentAmount.assetSymbol = amountSymbol;
                equivalentAmount.id = 0;

                EquivalentComponents equivalentFee = new EquivalentComponents();
                equivalentFee.Amount = Float.parseFloat(feeAmount);
                equivalentFee.assetSymbol = feeSymbol;
                equivalentFee.id = 1;

                ArrayList<EquivalentComponents> arrayList = new ArrayList<>();
                arrayList.add(equivalentAmount);
                arrayList.add(equivalentFee);

                getEquivalentComponents(arrayList);


                feeSymbol = assetsSymbols.updateString(sym_preFee.get("symbol"));
                amountSymbol = assetsSymbols.updateString(sym_preAmount.get("symbol"));

//                tvBlockNumber.setText(eReciptmap.get("block_num"));
//                tvTrxInBlock.setText(eReciptmap.get("id"));


                tvAmount.setText(amountAmount + " " + amountSymbol);
                tvFee.setText(feeAmount + " " + feeSymbol);
                tvTotal.setText(tvAmount.getText() + " + " + tvFee.getText());
                loadComplete = true;
            }
        });
    }

    void get_asset(String asset, String id) {
        //{"id":1,"method":"get_assets","params":[["1.3.0","1.3.120"]]}
        String getDetails = "{\"id\":" + id + ",\"method\":\"get_assets\",\"params\":[[\"" + asset + "\"]]}";
        Application.send(getDetails);
    }

    @OnClick(R.id.buttonSend)
    public void onSendButton() {
        btnPress = true;
//        checkifloadingComplete();
        generatepdfDoc();
    }

    String get_email(String accountName) {
        MerchantEmail merchantEmail = new MerchantEmail(this);
        return merchantEmail.getMerchantEmail(accountName);
    }

    @Override
    public void updateProfile(Gravatar myGravatar) {
        tvOtherCompany.setText(myGravatar.companyName);
        tvAddress.setText(myGravatar.address);
//        tvContact.setText(myGravatar.url);
    }

    @Override
    public void updateCompanyLogo(Bitmap logo) {
        Bitmap bitmap = getRoundedCornerBitmap(logo);
        ivOtherGravatar.setImageBitmap(bitmap);
    }

    @Override
    public void failureUpdateProfile() {

    }

    @Override
    public void failureUpdateLogo() {

    }

    GravatarDelegate instance() {
        return this;
    }

    void fetchGravatarInfo(String email) {
        Gravatar.getInstance(instance()).fetch(email);
    }

    private void getEquivalentComponents(final ArrayList<EquivalentComponents> equivalentComponentses) {

        String fiatCurrency = Helper.getFadeCurrency(this);

        if (fiatCurrency.isEmpty()) {
            fiatCurrency = "EUR";
        }

        String values = "";
        for (int i = 0; i < equivalentComponentses.size(); i++) {
            EquivalentComponents transactionDetails = equivalentComponentses.get(i);
            if (!transactionDetails.assetSymbol.equals(fiatCurrency)) {
                values += transactionDetails.assetSymbol + ":" + fiatCurrency + ",";
            }
        }

        if (values.isEmpty()) {
            return;
        }

        EquivalentFiatStorage equivalentFiatStorage = new EquivalentFiatStorage(this);
        HashMap hm = equivalentFiatStorage.getEqHM(fiatCurrency);

        try {
            for (int i = 0; i < equivalentComponentses.size(); i++) {
                String asset = equivalentComponentses.get(i).getAssetSymbol();
                String amount = String.valueOf(equivalentComponentses.get(i).getAmount());
                equivalentComponentses.get(i).available = false;
                if (!amount.isEmpty() && hm.containsKey(asset)) {
                    equivalentComponentses.get(i).available = true;
                    Currency currency = Currency.getInstance(fiatCurrency);
                    Double eqAmount = Double.parseDouble(amount) * Double.parseDouble(hm.get(asset).toString());
                    equivalentComponentses.get(i).fiatAssetSymbol = currency.getSymbol();
                    equivalentComponentses.get(i).fiatAmount = Float.parseFloat(String.format("%.4f", eqAmount));
                } else {
                    equivalentComponentses.get(i).fiatAssetSymbol = "";
                    equivalentComponentses.get(i).fiatAmount = 0f;
                }
            }
        } catch (Exception e) {
            ifEquivalentFailed();
        }

        setEquivalentComponents(equivalentComponentses);


    }

    void setEquivalentComponents(final ArrayList<EquivalentComponents> equivalentComponentse) {

        String value = "";
        Boolean available = false;

        EquivalentComponents equivalentAmount = equivalentComponentse.get(0);
        if (equivalentAmount.id == 0) {
            if (equivalentAmount.available) {
                available = true;
                tvAmountEquivalent.setText(equivalentAmount.fiatAmount + " " + equivalentAmount.fiatAssetSymbol);
            } else {
                available = false;
                tvAmountEquivalent.setVisibility(View.GONE);
                setWeight(tvAmount);
            }

            EquivalentComponents equivalentFee = equivalentComponentse.get(1);

            if (equivalentFee.id == 1) {
                if (available) {
                    tvFeeEquivalent.setText(equivalentFee.fiatAmount + " " + equivalentFee.fiatAssetSymbol);
                } else {
                    tvFeeEquivalent.setVisibility(View.GONE);
                    setWeight(tvFee);
                }
            }

            if (equivalentFee.id == 0) {
                if (equivalentAmount.available) {
                    available = true;
                    tvAmountEquivalent.setText(equivalentAmount.fiatAmount + " " + equivalentAmount.fiatAssetSymbol);
                } else {
                    available = false;
                    tvAmountEquivalent.setVisibility(View.GONE);
                    setWeight(tvAmount);
                }
            }

            if (equivalentAmount.id == 1) {
                if (available) {
                    tvFeeEquivalent.setText(equivalentFee.fiatAmount + " " + equivalentFee.fiatAssetSymbol);
                } else {
                    tvFeeEquivalent.setVisibility(View.GONE);
                    setWeight(tvFee);
                }
            }


            if (available) {
                tvTotalEquivalent.setText(equivalentAmount.fiatAmount + equivalentFee.fiatAmount + " " + equivalentAmount.fiatAssetSymbol);
            } else {
                tvTotalEquivalent.setText(value);
                setWeight(tvTotal);
            }
//            tvPaymentEquivalent.setText(tvTotalEquivalent.getText());
        }

    }

    void ifEquivalentFailed() {
        setWeight(tvAmount);
        setWeight(tvFee);
        setWeight(tvTotal);
    }

    void createEmail(String email, ImageView imageView) {
        String emailGravatarUrl = "https://www.gravatar.com/avatar/" + Helper.hash(email, Helper.MD5) + "?s=130&r=pg&d=404";
        new DownloadImageTask(imageView)
                .execute(emailGravatarUrl);
    }

    void setWeight(TextView textView) {
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.setLayoutParams(params);
    }

    private void generatePdf() {
        try {
            showProgressBar();
            verifyStoragePermissions(this);
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getResources().getString(R.string.folder_name) + File.separator + "eReceipt-" + transactionId + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            Bitmap bitmap = Bitmap.createBitmap(
                    llall.getWidth(),
                    llall.getHeight(),
                    Bitmap.Config.ARGB_8888);


            Canvas c = new Canvas(bitmap);
            llall.draw(c);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            Image myImage = Image.getInstance(imageInByte);
            float documentWidth = document.getPageSize().getWidth();
            float documentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            myImage.scaleToFit(documentWidth, documentHeight);
            myImage.setAlignment(Image.ALIGN_CENTER | Image.MIDDLE);
            document.add(myImage);
            document.close();
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    Uri uri = Uri.fromFile(new File(path));
                    email.putExtra(Intent.EXTRA_STREAM, uri);
                    email.putExtra(Intent.EXTRA_SUBJECT, "eReceipt " + date);
                    email.setType("application/pdf");
                    email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(email);
                }
            });
            hideProgressBar();
        } catch (Exception e) {
            Log.e(TAG, "Exception while tryig to share receipt info. Msg: " + e.getMessage());
        }
    }

    private void showDialog(String title, String msg) {
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog.setTitle(title);
                progressDialog.setMessage(msg);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
    }

    private void hideDialog() {

        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
        }


    }

    private void showProgressBar() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                buttonSend.setVisibility(View.GONE);
            }
        });
    }

    private void hideProgressBar() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.GONE);
                buttonSend.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void generatepdfDoc() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                generatePdf();
            }
        });
        t.start();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) bmImage.setVisibility(View.GONE);
            else {
                bmImage.setImageBitmap(result);
            }
        }
    }

    private class EquivalentComponents {
        int id;
        float Amount;
        String assetSymbol;
        float fiatAmount;
        Boolean available;
        String fiatAssetSymbol;

        float getAmount() {
            return this.Amount;
        }

        String getAssetSymbol() {
            return this.assetSymbol;
        }
    }
}