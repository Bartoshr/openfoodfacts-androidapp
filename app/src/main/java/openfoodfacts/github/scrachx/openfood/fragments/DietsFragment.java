package openfoodfacts.github.scrachx.openfood.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.DaoSession;
import openfoodfacts.github.scrachx.openfood.models.Diet;
import openfoodfacts.github.scrachx.openfood.models.DietDao;
import openfoodfacts.github.scrachx.openfood.views.OFFApplication;
import openfoodfacts.github.scrachx.openfood.views.adapters.DietsAdapter;
import openfoodfacts.github.scrachx.openfood.views.listeners.BottomNavigationListenerInstaller;

import static android.app.Activity.RESULT_OK;
import static openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.ITEM_DIET;

/**
 * @see R.layout#fragment_diets
 * Created by dobriseb on 2018.10.15.
 * Diet gest in local database.
 */
public class DietsFragment extends NavigationBaseFragment {

    private RecyclerView mRvDiet;
    private LinearLayout mEmptyMessageView; // Empty View containing the message that will be shown if there is no diet
    private LinearLayout mOneDietMessageView; // Empty View containing the message that will be shown if there is no diet
    private SharedPreferences mSettings;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    public int getNavigationDrawerType() {
        return ITEM_DIET;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return createView(inflater, container, R.layout.fragment_diets);
    }

    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmptyMessageView = view.findViewById(R.id.emptyDietsView);
        mOneDietMessageView = view.findViewById(R.id.oneDietView);
        //Looking for data to be load in the Recycler
        mRvDiet = (RecyclerView) view.findViewById(R.id.diets_recycler);
        DaoSession daoSession = OFFApplication.getInstance().getDaoSession();
        DietDao dietDao = daoSession.getDietDao();
        List<Diet> dietList = dietDao.loadAll();
        if (dietList.isEmpty()) {
            mEmptyMessageView.setVisibility(view.VISIBLE);
            mOneDietMessageView.setVisibility(view.GONE);
            mRvDiet.setVisibility(view.GONE);
        } else {
            mEmptyMessageView.setVisibility(view.GONE);
            mRvDiet.setVisibility(view.VISIBLE);
            if (dietList.size() == 1)
                mOneDietMessageView.setVisibility(view.VISIBLE);
            else
                mOneDietMessageView.setVisibility(view.GONE);
            //Activate the recycler with the good adapter
            mRvDiet.setLayoutManager(new LinearLayoutManager(this.getContext()));
            mRvDiet.setAdapter(new DietsAdapter(dietList, new ClickListener() {
                @Override
                public void onPositionClicked(int position, View v) {
                    Fragment fragment = new EditDietFragment();
                    if (v.getTag() != null) {
                        Bundle args = new Bundle();
                        args.putString("dietName", v.getTag().toString());
                        fragment.setArguments(args);
                    }
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, fragment );
                    transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                    transaction.commit();
                }
            }));
        }

        BottomNavigationListenerInstaller.install(bottomNavigationView,getActivity(),getContext());
    }

    /**
     * Add a diet.
     */
    @OnClick(R.id.fab)
    void openFragmentAddDiet () {
        Fragment fragment = new EditDietFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment );
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.your_diets));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onPositionClicked(int position, View v);
    }
}
