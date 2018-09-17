
package intermercato.com.keygenerator;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;



public class MainActivity2 extends AppCompatActivity {
    TabFragmentPrintLayout printLayoutFragment;
    TabFragmentPrintHelp printHelpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

    /*    Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);*/

        ViewPager viewPager = (ViewPager) findViewById(R.id.sample_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sample_tablayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        printLayoutFragment = new TabFragmentPrintLayout();
        printHelpFragment = new TabFragmentPrintHelp();

        adapter.addFrag(printLayoutFragment, getResources().getString(R.string.print_settings));
        adapter.addFrag(printHelpFragment, getResources().getString(R.string.printing_help));

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
