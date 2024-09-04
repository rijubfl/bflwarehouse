package com.bflgroup.warehouse;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bflgroup.warehouse.comm.Global;
import com.bflgroup.warehouse.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView tv_user_name;
    private Global objGlobal = Global.getInstance();
    private boolean b_Result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_chute_check_in_check_out, R.id.nav_chute_check_in_check_out_jafza, R.id.nav_chute_configuration_techno,
                R.id.nav_chute_configuration_jafza, R.id.nav_rack_in_out, R.id.nav_auto_building, R.id.nav_bin_storage_put_away, R.id.nav_bin_storage_put_away_history,
                R.id.nav_bin_storage_wave_pick, R.id.nav_bin_storage_batch_in, R.id.nav_build_usa_box, R.id.nav_build_pallet, R.id.nav_build_del_pallet,
                R.id.nav_divsion_seperate, R.id.nav_pallet_status, R.id.nav_r1_wh_grn, R.id.nav_shop_return, R.id.nav_3pl_wh_grn, R.id.nav_rack_in_out,
                R.id.nav_gin_verification, R.id.nav_blue_tote_transfer, R.id.nav_auto_building_jafza, R.id.nav_logoff, R.id.nav_bin_storage_put_away_multiple_tote,
                R.id.nav_build_del_gin, R.id.nav_transfer, R.id.nav_update_box_quantity, R.id.nav_blue_to_euro_box, R.id.nav_stock_taking, R.id.nav_generate_barcode,
                R.id.nav_shuttle_git, R.id.nav_shuttle_git, R.id.nav_stocktake, R.id.nav_pallet_box_count, R.id.nav_rack_query, R.id.nav_show_pallets, R.id.nav_warehouse_grn,
                R.id.nav_shuttle_task_create, R.id.nav_pallets_verification,R.id.nav_jafza_racks,R.id.nav_update_box_from_pallet,R.id.nav_gin_verify_local)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_chute_configuration_techno).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out_detail).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_chute_configuration_jafza).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out_jafza).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out_detail_jafza).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_auto_building).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_auto_building_jafza).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_pallet_status).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_bin_storage_batch_in).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_bin_storage_put_away).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_bin_storage_wave_pick).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_bin_storage_put_away_history).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_build_usa_box).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_build_pallet).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_build_del_pallet).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_gin_verification).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_r1_wh_grn).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_shop_return).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_divsion_seperate).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_3pl_wh_grn).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_r1_wh_grn).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_rack_in_out).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_blue_tote_transfer).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_bin_storage_put_away_multiple_tote).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_transfer).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_build_del_gin).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_update_box_quantity).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_blue_to_euro_box).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_stock_taking).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_generate_barcode).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_shuttle_git).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_stocktake).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_pallet_box_count).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_rack_query).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_show_pallets).setVisible(false);
       // navigationView.getMenu().findItem(R.id.nav_validate_toteid).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_build_box_barcode).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_warehouse_grn).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_shuttle_task_create).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_pallets_verification).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_department_grn).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_transfer_validate).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_jafza_racks).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_update_box_from_pallet).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_gin_verify_local).setVisible(false);

        for (int i = 0; i < objGlobal.getActiveMenuByUser().size(); i++) {
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_chute_configuration_techno")) {
                navigationView.getMenu().findItem(R.id.nav_chute_configuration_techno).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_chute_check_in_check_out")) {
                navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_chute_check_in_check_out_detail")) {
                navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out_detail).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_chute_configuration_jafza")) {
                navigationView.getMenu().findItem(R.id.nav_chute_configuration_jafza).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_chute_check_in_check_out_jafza")) {
                navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out_jafza).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_chute_check_in_check_out_detail_jafza")) {
                navigationView.getMenu().findItem(R.id.nav_chute_check_in_check_out_detail_jafza).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_auto_building")) {
                navigationView.getMenu().findItem(R.id.nav_auto_building).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_auto_building_jafza")) {
                navigationView.getMenu().findItem(R.id.nav_auto_building_jafza).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_pallet_status")) {
                navigationView.getMenu().findItem(R.id.nav_pallet_status).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_bin_storage_batch_in")) {
                navigationView.getMenu().findItem(R.id.nav_bin_storage_batch_in).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_bin_storage_put_away")) {
                navigationView.getMenu().findItem(R.id.nav_bin_storage_put_away).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_bin_storage_wave_pick")) {
                navigationView.getMenu().findItem(R.id.nav_bin_storage_wave_pick).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_bin_storage_put_away_history")) {
                navigationView.getMenu().findItem(R.id.nav_bin_storage_put_away_history).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_build_usa_box")) {
                navigationView.getMenu().findItem(R.id.nav_build_usa_box).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_build_pallet")) {
                navigationView.getMenu().findItem(R.id.nav_build_pallet).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_build_del_pallet")) {
                navigationView.getMenu().findItem(R.id.nav_build_del_pallet).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_gin_verification")) {
                navigationView.getMenu().findItem(R.id.nav_gin_verification).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_r1_wh_grn")) {
                navigationView.getMenu().findItem(R.id.nav_r1_wh_grn).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_shop_return")) {
                navigationView.getMenu().findItem(R.id.nav_shop_return).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_divsion_seperate")) {
                navigationView.getMenu().findItem(R.id.nav_divsion_seperate).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_3pl_wh_grn")) {
                navigationView.getMenu().findItem(R.id.nav_3pl_wh_grn).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_r1_wh_grn")) {
                navigationView.getMenu().findItem(R.id.nav_r1_wh_grn).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_rack_in_out")) {
                navigationView.getMenu().findItem(R.id.nav_rack_in_out).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_blue_tote_transfer")) {
                navigationView.getMenu().findItem(R.id.nav_blue_tote_transfer).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_bin_storage_put_away_multiple_tote")) {
                navigationView.getMenu().findItem(R.id.nav_bin_storage_put_away_multiple_tote).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_transfer")) {
                navigationView.getMenu().findItem(R.id.nav_transfer).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_build_del_gin")) {
                navigationView.getMenu().findItem(R.id.nav_build_del_gin).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_update_box_quantity")) {
                navigationView.getMenu().findItem(R.id.nav_update_box_quantity).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_blue_to_euro_box")) {
                navigationView.getMenu().findItem(R.id.nav_blue_to_euro_box).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_stock_taking")) {
                navigationView.getMenu().findItem(R.id.nav_stock_taking).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_generate_barcode")) {
                navigationView.getMenu().findItem(R.id.nav_generate_barcode).setVisible(true);
            }
            /*if (objGlobal.getActiveMenuByUser().get(i).equals("nav_shuttle_git")) {
                navigationView.getMenu().findItem(R.id.nav_shuttle_git).setVisible(true);
            }*/
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_stocktake")) {
                navigationView.getMenu().findItem(R.id.nav_stocktake).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_pallet_box_count")) {
                navigationView.getMenu().findItem(R.id.nav_pallet_box_count).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_rack_query")) {
                navigationView.getMenu().findItem(R.id.nav_rack_query).setVisible(true);
            }

            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_show_pallets")) {
                navigationView.getMenu().findItem(R.id.nav_show_pallets).setVisible(true);
            }

            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_build_box_barcode")) {
                navigationView.getMenu().findItem(R.id.nav_build_box_barcode).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_warehouse_grn")) {
                navigationView.getMenu().findItem(R.id.nav_warehouse_grn).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_shuttle_task_create")) {
                navigationView.getMenu().findItem(R.id.nav_shuttle_task_create).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_pallets_verification")) {
                navigationView.getMenu().findItem(R.id.nav_pallets_verification).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_department_grn")) {
                navigationView.getMenu().findItem(R.id.nav_department_grn).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_update_box_from_pallet")) {
                navigationView.getMenu().findItem(R.id.nav_update_box_from_pallet).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_transfer_validate")) {
                navigationView.getMenu().findItem(R.id.nav_transfer_validate).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_jafza_racks")) {
                navigationView.getMenu().findItem(R.id.nav_jafza_racks).setVisible(true);
            }
            if (objGlobal.getActiveMenuByUser().get(i).equals("nav_gin_verify_local")) {
                navigationView.getMenu().findItem(R.id.nav_gin_verify_local).setVisible(true);
            }
        }
        navigationView.getMenu().findItem(R.id.nav_settings).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logoff).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_validate_toteid).setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
