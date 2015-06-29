package com.example.rukeon01.daum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private InputMethodManager imm; // for keybaord control
    private AutoCompleteTextView autoSearch;
    private MapView mapView;

    // 자동검색 완성을 위한 단어모음
    String[] autoSearchWords = new String[] {
            "58동", "1동", "2동", "3동", "인문신양", "301동", "학생회관",
            "12동", "19동"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 메뉴 버튼 클릭시...
        final Button popupButton = (Button) findViewById(R.id.btn_menu);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.show();
            }

            ;
        });

        // 자동검색을 위한 어댑터 부착
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < autoSearchWords.length; ++i) {
            list.add(autoSearchWords[i]);
        }

        final ArrayAdapter<String> sWords = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        autoSearch = (AutoCompleteTextView) findViewById(R.id.autoTxt_Search);
        autoSearch.setAdapter(sWords);

        // 자동검색 바 클릭시 검색어 없애기
        autoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AutoCompleteTextView autoSearch = (AutoCompleteTextView) findViewById(R.id.autoTxt_Search);
                autoSearch.setText("");
                showKeyboard();
                LinearLayout list_menu = (LinearLayout) findViewById(R.id.list_menu);
                list_menu.setVisibility(View.GONE);
            }
        });

        // 자동검색 목록에서 나온 것 클릭 시..
        autoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this,
                        sWords.getItem(position).toString(),
                        Toast.LENGTH_SHORT).show();
                System.out.println("나온다~~~~" + sWords.getItem(position).toString());
                hideKeyboard();
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.460316, 126.952338), 1, true);

                MapPOIItem marker = new MapPOIItem();
                marker.setItemName("Default Marker");
                marker.setTag(0);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.460316, 126.952338));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                mapView.addPOIItem(marker);

                LinearLayout list_menu = (LinearLayout) findViewById(R.id.list_menu);
                list_menu.setVisibility(View.VISIBLE);
            }
        });

        // 다음 api 관련 코드
        mapView = new MapView(this);
        mapView.setDaumMapApiKey("63410ce315710edb71bc2394b408d28b");

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.459882, 126.951905), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(3, true);

        // 키보드 관리를 위한 시작, 아래 함수 있다.
        init();

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(MainActivity.this,
                                "등록되었습니다. 메뉴->내 목록에서 확인가능합니다",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        // 즐겨찾기 버튼 클릭 이벤트 처리
        LinearLayout favorite = (LinearLayout) findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setMessage("1동을 즐겨찾는 건물에 등록하시겠습니까?").setPositiveButton("확인", dialogClickListener).setNegativeButton("취소", dialogClickListener).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void init(){
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        autoSearch = (AutoCompleteTextView) findViewById(R.id.autoTxt_Search);
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard(){
        imm.showSoftInput(autoSearch, 0);
    }
}
