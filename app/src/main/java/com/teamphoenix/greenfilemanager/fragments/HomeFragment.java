package com.teamphoenix.greenfilemanager.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.teamphoenix.greenfilemanager.R;
import com.teamphoenix.greenfilemanager.adapter.FileRecyclerViewAdapter;
import com.teamphoenix.greenfilemanager.databinding.FragmentHomeBinding;
import com.teamphoenix.greenfilemanager.listener.onFileSelectedListener;
import com.teamphoenix.greenfilemanager.utils.FileOpener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements onFileSelectedListener {
    private FragmentHomeBinding homeBinding;
    private List<File> fileList;
    private File storage;
    private FileRecyclerViewAdapter fileAdapter;
    private String data;
    private String[] clickMenu = {"Details", "Rename", "Delete", "Share"};
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }
    private void runtimePermission() {

        Dexter.withContext(getContext())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displayFiles();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findFiles(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                fileArrayList.add(singleFile);
            }
        }
        for (File singleFile : files) {
            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") ||
                    singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3") ||
                    singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4") ||
                    singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc") ||
                    singleFile.getName().toLowerCase().endsWith(".apk")) {
                fileArrayList.add(singleFile);
            }
        }
        return fileArrayList;
    }

    private void displayFiles() {
        homeBinding.homeRecycler.setHasFixedSize(true);
        homeBinding.homeRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(storage));
        fileAdapter = new FileRecyclerViewAdapter(getContext(), fileList, this);
        homeBinding.homeRecycler.setAdapter(fileAdapter);
        fileAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalStorageFragment internalStorageFragment = new InternalStorageFragment();
            internalStorageFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, internalStorageFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionsDialog = new Dialog(getContext());
        optionsDialog.setContentView(R.layout.dialog_click_menu);
        optionsDialog.setTitle("Select Options.");
        ListView optionsListView = optionsDialog.findViewById(R.id.dialog_listview);
        CustomOptionsMenuAdapter menuAdapter = new CustomOptionsMenuAdapter();
        optionsListView.setAdapter(menuAdapter);
        optionsDialog.show();

        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMenu = adapterView.getItemAtPosition(i).toString();

                switch (selectedMenu) {
                    case "Details":
                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Details");
                        final TextView details = new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified = new Date(file.lastModified());
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
                        String formattedDate = dateFormatter.format(lastModified);

                        details.setText("File Name: " + file.getName() +
                                "\n" + "File Size: " + Formatter.formatShortFileSize(getContext(), file.length()) +
                                "\n" + "Path: " + file.getAbsolutePath() +
                                "\n" + "Last Modified: " + formattedDate);
                        details.setPadding(15, 15, 8, 8);
                        detailDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionsDialog.cancel();
                            }
                        });
                        AlertDialog alertDialog_details = detailDialog.create();
                        alertDialog_details.show();
                        break;
                    case "Rename":
                        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Rename");
                        final EditText renameEditText = new EditText(getContext());
                        renameEditText.setPadding(8, 8, 8, 8);
                        renameEditText.setText(file.getName().toString());
                        renameDialog.setView(renameEditText);
                        renameDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String newName = renameEditText.getEditableText().toString();
                                String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

                                File currentFile = new File(file.getAbsolutePath());
                                File destinationFile = new File(file.getAbsolutePath().replace(file.getName(), newName) + extension);
                                if (currentFile.renameTo(destinationFile)) {
                                    fileList.set(position, destinationFile);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Renamed file to " + newName + extension, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Could not Rename the file", Toast.LENGTH_SHORT).show();
                                }
                                optionsDialog.cancel();
                            }
                        });
                        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionsDialog.cancel();
                            }
                        });

                        AlertDialog alertDialog_renameDialog = renameDialog.create();
                        alertDialog_renameDialog.show();
                        break;
                    case "Delete":
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                        deleteDialog.setTitle("Delete " + file.getName() + "?");
                        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "File has been deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                optionsDialog.cancel();
                            }
                        });

                        AlertDialog alertDialogDelete = deleteDialog.create();
                        alertDialogDelete.show();
                        break;
                    case "Share":
                        Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("image/jpeg");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        startActivity(Intent.createChooser(intent, " Share: " + file.getName()));
                        optionsDialog.cancel();
                        break;
                }
            }
        });
    }

    class CustomOptionsMenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return clickMenu.length;
        }

        @Override
        public Object getItem(int i) {
            return clickMenu[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View mView = getLayoutInflater().inflate(R.layout.item_option_menu_container, null);
            TextView optionName = mView.findViewById(R.id.item_option_text);
            ImageView optionImage = mView.findViewById(R.id.item_option_img);

            optionName.setText(clickMenu[i]);
            if (clickMenu[i].equals("Details")) {
                optionImage.setImageResource(R.drawable.ic_details);
            } else if (clickMenu[i].equals("Rename")) {
                optionImage.setImageResource(R.drawable.ic_rename);
            } else if (clickMenu[i].equals("Delete")) {
                optionImage.setImageResource(R.drawable.ic_delete);
            } else if (clickMenu[i].equals("Share")) {
                optionImage.setImageResource(R.drawable.ic_share);
            }
            return mView;
        }
    }
}
