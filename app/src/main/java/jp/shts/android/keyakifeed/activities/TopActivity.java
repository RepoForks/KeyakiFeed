package jp.shts.android.keyakifeed.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import jp.shts.android.keyakifeed.R;
import jp.shts.android.keyakifeed.databinding.ActivityTopBinding;
import jp.shts.android.keyakifeed.fragments.AllFeedListFragment;
import jp.shts.android.keyakifeed.fragments.AllMemberGridFragment;
import jp.shts.android.keyakifeed.fragments.FavoriteMemberFeedListFragment;
import jp.shts.android.keyakifeed.fragments.MatomeFeedListFragment;
import jp.shts.android.keyakifeed.fragments.OfficialReportListFragment;
import jp.shts.android.keyakifeed.fragments.SettingsFragment;
import jp.shts.android.keyakifeed.utils.PreferencesUtils;

public class TopActivity extends AppCompatActivity {

    private static final String TAG = TopActivity.class.getSimpleName();

    private ActivityTopBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_top);

        binding.toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawer.openDrawer(binding.navigation);
            }
        });

        binding.navigation.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        binding.drawer.closeDrawers();
                        final int id = menuItem.getItemId();
                        if (id == getLastSelectedMenuId()) {
                            return false;
                        }
                        setupFragment(id);
                        return false;
                    }
                });
        setupFragment(getLastSelectedMenuId());
    }

    private int getLastSelectedMenuId() {
        return PreferencesUtils.getInt(this, "pre-fragment", R.id.menu_all_feed);
    }

    private void setLastSelectedMenuId(int id) {
        PreferencesUtils.setInt(this, "pre-fragment", id);
    }

    private void setupFragment(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.menu_all_feed:
                fragment = new AllFeedListFragment();
                break;
            case R.id.menu_fav_member_feed:
                fragment = new FavoriteMemberFeedListFragment();
                break;
            case R.id.menu_report:
                fragment = new OfficialReportListFragment();
                break;
            case R.id.menu_member:
                fragment = AllMemberGridFragment.newInstance(
                        AllMemberGridFragment.ListenerType.START_DETAIL);
                break;
            case R.id.menu_matome:
                fragment = new MatomeFeedListFragment();
                break;
            case R.id.menu_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.menu_request:
            case R.id.menu_about_app:
            case R.id.menu_lisences:
                startActivity(OtherMenuActivity.getStartIntent(this, id));
                return;
            default:
                Log.e(TAG, "failed to change fragment");
                return;
        }

        binding.navigation.getMenu().findItem(id).setChecked(true);
        binding.toolbar.setTitle(
                binding.navigation.getMenu().findItem(id).getTitle());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment, fragment.toString());
        ft.commit();
        setLastSelectedMenuId(id);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
            return;
        }
        boolean currentPageHome = binding.navigation.getMenu().findItem(
                R.id.menu_all_feed).isChecked();
        if (!currentPageHome) {
            setHomeFragment();
            return;
        }
        super.onBackPressed();
    }

    private void setHomeFragment() {
        binding.navigation.getMenu().findItem(R.id.menu_all_feed).setChecked(true);
        binding.toolbar.setTitle(
                binding.navigation.getMenu().findItem(R.id.menu_all_feed).getTitle());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new AllFeedListFragment(), AllFeedListFragment.class.getSimpleName());
        ft.commit();
        setLastSelectedMenuId(R.id.menu_all_feed);
    }
}
