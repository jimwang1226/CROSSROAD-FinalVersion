package com.community.protectcommunity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HelpSearchResultFragment extends Fragment implements View.OnClickListener{
    private View helpResearchResultFragment;
    private Button backToParentsButton;
    private ListView hospitalListView;
    private ListView counsellingListView;
    private SoundPool soundPool;//declare a SoundPool
    private int soundID;//Create an audio ID for a sound
    private SharedPreferences sharedPref;
    private List<Hospital> hospitalList = new ArrayList<Hospital>();
    private List<Counselling> counsellingList = new ArrayList<Counselling>();
    private int finalScore = 0;
    private int postcode = 0;

    BackToMainScreenPopup popup;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        helpResearchResultFragment = LayoutInflater.from(getActivity()).inflate(R.layout.help_search_result_fragment,
                container, false);
        //Initialize
        soundID = SoundUtil.initSound(this.getActivity());
        sharedPref = getActivity().getSharedPreferences("help_function",Context.MODE_PRIVATE);
        finalScore = sharedPref.getInt("finalScore", 0);
        String postcodeStr = sharedPref.getString("postcode", null);
        postcode = Integer.valueOf(postcodeStr);
        setView();
        getHospitalDataFromMysql();
        getCounsellingDataFromMysql();
        return helpResearchResultFragment;
    }

    public void setView () {
        //initialize the view
        backToParentsButton = (Button)helpResearchResultFragment.findViewById(R.id.help_return_to_for_parents_screen_button);
        hospitalListView = (ListView)helpResearchResultFragment.findViewById(R.id.help_search_result_left_lv);
        counsellingListView = (ListView)helpResearchResultFragment.findViewById(R.id.help_search_result_right_lv);
        backToParentsButton.setOnClickListener(this);

        //set up the view
        sharedPref = getActivity().getSharedPreferences("help_function",Context.MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        Fragment nextFragment;
        FragmentManager fragmentManager = getFragmentManager();
        nextFragment = new HelpQuestionThreeFragment();
        switch (view.getId()) {
            case R.id.help_return_to_for_parents_screen_button:
                SoundUtil.playSound(soundID);
                //might cause exception
                //mHomeWatcher.stopWatch();
                getResultFragment();
                break;
            default:
                break;
        }
    }

    //go back to parents activity
    public void getParents(){
        Intent intent = new Intent(this.getContext(), ForParentsActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }

    //go back to main screen
    public void getHome(){
        Intent intent = new Intent(this.getContext(), MainScreenActivity.class);
        this.getContext().startActivity(intent);
        this.getActivity().finish();
        //this.getContentView()
    }

    //go back to result fragment
    public void getResultFragment() {
        HelpResultFragment nextFragment = new HelpResultFragment();
        FragmentManager fragmentManager = getFragmentManager();
        //change the background of help result fragment
        nextFragment.setScore(finalScore);
        //change the url link in help result fragment
        String ageGroup = sharedPref.getString("ageGroup", null);

        fragmentManager.beginTransaction().setCustomAnimations(R.animator.slide_in,R.animator.slide_in_opp,R.animator.slide_out_opp,
                R.animator.slide_out).replace(R.id.help_change_area, nextFragment).commit();
    }

    //initialize the return pop up window
    public void initPopupLayout() {
        popup = new BackToMainScreenPopup(this.getContext(), getActivity());
        popup.showPopupWindow();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void getHospitalDataFromMysql() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start!!!!!!!!!");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn = DriverManager.getConnection(
                            "jdbc:mysql://35.244.125.144:3306/protect_community", "root",
                            "wangjinge2007");
                    int lowest = postcode - 20;
                    int highest = postcode + 20;
                    String sqlFemale = "select name, link, specialty from hospital where postcode>" + lowest +  " and postcode<" + highest;
                    System.out.println(sqlFemale);
                    Statement st = (Statement) cn.createStatement();
                    ResultSet rs = st.executeQuery(sqlFemale);
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String link = rs.getString("link");
                        String specialty = "SPECIALTY: \n" + rs.getString("specialty");
                        specialty = specialty.replaceAll("/", ", ");
                        Hospital hospital = new Hospital(name, specialty, link);
                        hospitalList.add(hospital);
                        System.out.println(name + " " + link + " " + specialty);
                    }
                    if (hospitalList.size() == 0) {
                        Hospital hospital = new Hospital("No related hospital in your area.", "", "");
                        hospitalList.add(hospital);
                    }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("flag");
                                    HospitalAdapter adapter = new HospitalAdapter(getContext(),
                                            R.layout.list_view_item_hospital_layout, hospitalList);
                                    hospitalListView.setAdapter(adapter);
                                    System.out.println("flag1");
                                    hospitalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            //for testing
                                            //Toast.makeText(FactSheetActivity.this, "点击的子项ID = " + position, Toast.LENGTH_SHORT).show();
                                            for(int i = 0 ; i < hospitalList.size() ; i++) {
                                                if(position == i) {
                                                    String url = hospitalList.get(i).getUrl();
                                                    if (!url.equals("")) {
                                                        LeaveThisPagePopup thisPagePopup = new LeaveThisPagePopup(getContext(), getActivity(), url);
                                                        thisPagePopup.showPopupWindow();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                    cn.close();
                    st.close();
                    rs.close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("class not found");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("connection error");
                }
            }
        }).start();
    }

    public void getCounsellingDataFromMysql() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start!!!!!!!!!");
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection cn = DriverManager.getConnection(
                            "jdbc:mysql://35.244.125.144:3306/protect_community", "root",
                            "wangjinge2007");
                    int lowest = postcode - 20;
                    int highest = postcode + 20;
                    String sqlFemale = "select name, link from counselling where postcode>" + lowest + " and postcode<" + highest;
                    Statement st = (Statement) cn.createStatement();
                    ResultSet rs = st.executeQuery(sqlFemale);
                    while (rs.next()) {
                        String name = rs.getString("name");
                        String link = rs.getString("link");
                        Counselling counselling = new Counselling(name, link);
                        counsellingList.add(counselling);
                        System.out.println(name + " " + link);
                    }
                    if (counsellingList.size() == 0) {
                        Counselling counselling = new Counselling("No related hospital in your area.", "");
                        counsellingList.add(counselling);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("flag");
                            CounsellingAdapter adapter = new CounsellingAdapter(getContext(),
                                    R.layout.list_view_item_counselling_layout, counsellingList);
                            counsellingListView.setAdapter(adapter);
                            System.out.println("flag1");
                            counsellingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //for testing
                                    //Toast.makeText(FactSheetActivity.this, "点击的子项ID = " + position, Toast.LENGTH_SHORT).show();
                                    for(int i = 0 ; i < counsellingList.size() ; i++) {
                                        if(position == i) {
                                            String url = counsellingList.get(i).getUrl();
                                            if (!url.equals("")) {
                                                LeaveThisPagePopup thisPagePopup = new LeaveThisPagePopup(getContext(), getActivity(), url);
                                                thisPagePopup.showPopupWindow();
                                            }
                                            /**
                                            if (!url.equals("")) {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                startActivity(browserIntent);
                                            }
                                             */
                                        }
                                    }
                                }
                            });
                        }
                    });
                    cn.close();
                    st.close();
                    rs.close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("class not found");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("connection error");
                }
            }
        }).start();
    }
}