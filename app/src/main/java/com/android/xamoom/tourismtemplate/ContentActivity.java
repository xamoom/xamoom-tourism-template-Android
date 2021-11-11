package com.android.xamoom.tourismtemplate;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.xamoom.tourismtemplate.fragments.ContentFragment;
import com.android.xamoom.tourismtemplate.fragments.ScannerFragment;
import com.android.xamoom.tourismtemplate.utils.ApiUtil;
import com.android.xamoom.tourismtemplate.utils.ExtensionsKt;
import com.android.xamoom.tourismtemplate.utils.InAppNotificationUtil;
import com.android.xamoom.tourismtemplate.utils.QuizUtil;
import com.xamoom.android.xamoomcontentblocks.XamoomContentFragment;
import com.xamoom.android.xamoomsdk.Resource.Content;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContentActivity extends AppCompatActivity implements ContentFragment.ContentFragmentListener,
        XamoomContentFragment.OnXamoomContentFragmentInteractionListener, ScannerFragment.QrScannerListener {
  public final static String EXTRA_CONTENT = "content";
  public final static String EXTRA_CONTENTID = "contentid";
  public final static String EXTRA_LOCID = "locid";
  public final static String EXTRA_TAG = "tag";
  public final static String EXTRA_BEACON_MINOR = "beacon";
  public final static String EXTRA_IS_BEACON = "isBeacon";
  private static final int REQUEST_CAMERA = 2;


  private boolean scannerOpened = false;

  @BindView(R.id.content_root_layout) View rootLayout;
  private ContentFragment contentFragment;

  @Inject
  SharedPreferences sharedPreferences;

  private NfcAdapter nfcAdapter;
  private PendingIntent pendingIntent;
  private IntentFilter[] intentFiltersArray;
  private String[][] techListsArray;
  private Content content;
  private boolean isQuizFeatureEnabled = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    MainApp.app().getAppComponent().inject(this);
    setContentView(R.layout.activity_content);

    ButterKnife.bind(this);

    ApiUtil.getInstance().setInAppNotificationUtil(
            new InAppNotificationUtil(this, rootLayout));

    contentFragment = ContentFragment.newInstance();

    String id = null;
    if (getIntent() != null && getIntent().getExtras() != null) {
      Bundle extras = getIntent().getExtras();

      if (extras != null) {
        contentFragment.setIsBeacon(extras.getBoolean(EXTRA_IS_BEACON, false));

        if (extras.getParcelable(ContentActivity.EXTRA_CONTENT) != null) {
          contentFragment.setContent((Content) extras.getParcelable(ContentActivity.EXTRA_CONTENT));
          id = ((Content) extras.getParcelable(ContentActivity.EXTRA_CONTENT)).getId();
        }
        else if (extras.getString(ContentActivity.EXTRA_CONTENTID) != null) {
          contentFragment.setContentId(extras.getString(ContentActivity.EXTRA_CONTENTID));
        }
        else if (extras.getString(ContentActivity.EXTRA_LOCID) != null) {
          contentFragment.setLocId(extras.getString(ContentActivity.EXTRA_LOCID));
        }
        if (extras.getInt(ContentActivity.EXTRA_BEACON_MINOR, -1) != -1) {
          contentFragment.setMinor(extras.getInt(EXTRA_BEACON_MINOR));
          id = String.valueOf(extras.getInt(EXTRA_BEACON_MINOR));
        }

        if (extras.getString(ContentActivity.EXTRA_TAG, null) != null) {
          contentFragment.setTag(extras.getString(EXTRA_TAG));
          id = extras.getString(EXTRA_TAG);
        }
      }
    }

    content = contentFragment.getContent();

    if(content != null && content.getTags() != null && content.getTags().contains(Globals.VOUCHER_TAG.toUpperCase())) {
      nfcAdapter = NfcAdapter.getDefaultAdapter(this);
      pendingIntent = PendingIntent.getActivity(
              this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
      IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
      try {
        ndef.addDataType("text/plain");
      }
      catch (IntentFilter.MalformedMimeTypeException e) {
        throw new RuntimeException("fail", e);
      }
      intentFiltersArray = new IntentFilter[] {ndef, };
      techListsArray = new String[][] { new String[] { NfcF.class.getName() } };
    }

    isQuizFeatureEnabled = getResources().getString(R.string.enable_quiz_feature).equals("true");

    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_main_frame_layout, contentFragment)
            .commit();
  }

  private void openScanner() {
    ScannerFragment scannerFragment = ScannerFragment.Companion.newInstance(this);
    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_main_frame_layout, scannerFragment)
            .commit();
    scannerOpened = true;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_CAMERA) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
          openScanner();
        }
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @Override
  public void finishActivity() {
    finish();
  }

  @Override
  public void clickedScanVoucher() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
      openScanner();
    }
    else {
      this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }
  }

  @Override
  public void clickedContentBlock(Content content) {
    Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
    intent.putExtra(ContentActivity.EXTRA_CONTENT, content);
    startActivity(intent);
  }

  @Override
  public void clickedSpotMapContentLink(String contentId) {
    Content content = new Content();
    content.setId(contentId);
    clickedContentBlock(content);
  }

  //Implemented in Quiz apps only
  @Override
  public void onQuizHtmlResponse(String html) {
    List<String> tags = content.getTags();
    if(isQuizFeatureEnabled && (tags.contains(Globals.QUIZ_TAG) || tags.contains(Globals.QUIZ_TAG.toUpperCase()))) {
      String points = getPoints(html);
      if(points != null) {
        if(Integer.parseInt(points) > 0) {
          QuizUtil quizUtil = new QuizUtil(this);
          try {
            quizUtil.increaseQuizPoints(Integer.parseInt(points));
            quizUtil.saveSubmittedQuiz(new QuizUtil.Quiz(content.getId(), new Date()));
            quizUtil.increaseVoucherAmount(Integer.parseInt(points));

            String explanation = getAnswerExplanation(html);
            if(explanation != null)
              showQuizRightAnswerAlert(points, explanation);
            else showQuizRightAnswerAlert(points, "");
            playSound(R.raw.quiz_answer_correct);
          } catch (NumberFormatException e) {
            Toast.makeText(this, "Error while parsing the Form response", Toast.LENGTH_LONG).show();
          }
        } else {
          showQuizWrongAnswerAlert();
          playSound(R.raw.quiz_answer_incorrect);
        }
      }
    }
  }

  private void playSound(int resId) {
    MediaPlayer mPlayer = MediaPlayer.create(ContentActivity.this, resId);
    mPlayer.start();
  }

  private void showQuizRightAnswerAlert(String points, String successMessage) {
    ExtensionsKt.updateLocalizations(this, sharedPreferences.getString("current_language_code", null));
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.quiz_right_answer_title));
    builder.setMessage(points + "\n" +
            getString(R.string.quiz_right_answer_subtitle) + "\n" +
            successMessage);

    builder.setPositiveButton(R.string.quiz_answer_button_goToScore, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        startActivity(new Intent(getApplicationContext(), QuizScoreActivity.class));
      }
    });
    builder.setNegativeButton(R.string.quiz_answer_button_close, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) { }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  private void showQuizWrongAnswerAlert() {
    ExtensionsKt.updateLocalizations(this, sharedPreferences.getString("current_language_code", null));
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.quiz_wrong_answer_title));
    builder.setMessage(R.string.quiz_wrong_answer_subtitle);
    builder.setPositiveButton(R.string.quiz_answer_button_close, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) { }
    });
    AlertDialog dialog = builder.create();
    dialog.show();
  }

  private String getAnswerExplanation(String htmlResponse) {
    Pattern pattern = Pattern.compile("(?<=gquiz-answer-explanation\">)[^<]*");
    Matcher matcher = pattern.matcher(htmlResponse);
    if (matcher.find()) {
      return htmlResponse.substring(matcher.start(), matcher.end()).trim();
    }
    return "";
  }

  private String getPoints(String htmlResponse) {
    int indexOfPass = htmlResponse.indexOf("id=\"score\"");
    if (indexOfPass != -1) {
      String stringAfterPassValue = htmlResponse.substring(indexOfPass);
      ArrayList<Integer> indexesOfPointsValue = new ArrayList<>();
      for (int i = 1; i < stringAfterPassValue.length(); i++) {
        if (Character.isDigit(stringAfterPassValue.charAt(i))) {
          indexesOfPointsValue.add(i);
          continue;
        }
        if (!Character.isDigit(stringAfterPassValue.charAt(i)) && Character.isDigit(stringAfterPassValue.charAt(i - 1))) {
          break;
        }
      }
      StringBuilder pointsValue = new StringBuilder();
      for (int index :
              indexesOfPointsValue) {
        pointsValue.append(stringAfterPassValue.charAt(index));
      }

      return pointsValue.toString();
    }
    return null;
  }

  @Override
  public void onBackPressed() {
    if (scannerOpened) {
      getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.content_main_frame_layout, contentFragment)
              .commit();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public void openContent(@NotNull Content content) {
  }

  @Override
  public void openContentId(@NotNull String contentId) {
  }

  @Override
  public void openLocId(@NotNull String locId) {
  }

  @Override
  public void handleOtherScanResult(@NotNull String resultText) {
    contentFragment.gotScanResult(resultText);
    getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_main_frame_layout, contentFragment)
            .commit();
    scannerOpened = false;
  }


  @Override
  protected void onPause() {
    super.onPause();
    if(content != null && content.getTags() != null &&content.getTags().contains(Globals.VOUCHER_TAG.toUpperCase())) {
      if(nfcAdapter != null)
      nfcAdapter.disableForegroundDispatch(this);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(content != null && content.getTags() != null && content.getTags().contains(Globals.VOUCHER_TAG.toUpperCase())) {
      if(nfcAdapter != null)
      nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    contentFragment.showVoucherNfcRedemptionAlert(messages);
  }

}
