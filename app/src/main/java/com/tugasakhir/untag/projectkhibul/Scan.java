package com.tugasakhir.untag.projectkhibul;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;
import com.google.cloud.translate.*;
import com.google.cloud.translate.Translate;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Scan.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Scan#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Scan extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String GOOGLE_API_KEY = "AIzaSyBuJ_62Ar-N_70VfA2Xm5gG8nxRzDgRyco";

    private CropImageView mCropImageView;
    public ImageView imageview;
    TextToSpeech t1;
    FloatingActionButton btnTTS;
    public EditText edJapan,edIndo;

    public Bitmap bitmap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Vision.Builder visionBuilder = new Vision.Builder(
            new NetHttpTransport(),
            new AndroidJsonFactory(),
            null
    ).setVisionRequestInitializer(new VisionRequestInitializer(GOOGLE_API_KEY));
    private Vision vision;


    private OnFragmentInteractionListener mListener;

    public Scan() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Scan.
     */
    // TODO: Rename and change types and number of parameters
    public static Scan newInstance(String param1, String param2) {
        Scan fragment = new Scan();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Halaman Scan");



        vision = visionBuilder.build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_scan, container, false);

        imageview = (ImageView) v.findViewById(R.id.imgViewScan);
        btnTTS = (FloatingActionButton)v.findViewById(R.id.btnTTS);

        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.JAPAN);
                }
            }
        });

        btnTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = edJapan.getText().toString();
                Toast.makeText(getContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        mCropImageView = (CropImageView)  v.findViewById(R.id.CropImageView);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Toast.makeText(getContext(), "pilih sesuai yang diinginkan", Toast.LENGTH_SHORT).show();
                PickImageDialog.build(new PickSetup())
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(final PickResult r) {
                                //TODO: do what you have to...
                                imageview.setImageURI(r.getUri());
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            InputStream inputStream = new FileInputStream(r.getPath());;
                                            Log.d("Ikko", "run: "+inputStream);
                                            byte[] photoData = org.apache.commons.io.IOUtils.toByteArray(inputStream);

                                            Image inputImage = new Image();
                                            inputImage.encodeContent(photoData);

                                            Feature desiredFeature = new Feature();
                                            desiredFeature.setType("TEXT_DETECTION");

                                            AnnotateImageRequest request = new AnnotateImageRequest();
                                            request.setImage(inputImage);
                                            request.setFeatures(Arrays.asList(desiredFeature));

                                            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                                            batchRequest.setRequests(Arrays.asList(request));

                                            BatchAnnotateImagesResponse batchResponse =
                                                    vision.images().annotate(batchRequest).execute();

//                                            List<FaceAnnotation> faces = batchResponse.getResponses()
//                                                    .get(0).getFaceAnnotations();

//                                            Translate translate = createTranslateService();
//                                            Translation translation = translate.translate(sourceText);

                                            Log.d("ikko", "run: "+batchResponse.getResponses());
                                            final TextAnnotation text = batchResponse.getResponses()
                                                    .get(0).getFullTextAnnotation();

//                                            int numberOfFaces = faces.size();
//
//
//
//                                            String likelihoods = "";
//                                            for(int i=0; i<numberOfFaces; i++) {
//                                                likelihoods += "\n It is " +
//                                                        faces.get(i).getJoyLikelihood() +
//                                                        " that face " + i + " is happy";
//                                            }
//
//                                            final String message =
//                                                    "This photo has " + numberOfFaces + " faces" + likelihoods;


                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    edJapan.setText(text.getText());

                                                    Toast.makeText(getContext(),
                                                            text.getText(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                        } catch (Exception e){
                                            Log.d("ERROR", e.getMessage());
                                        }
                                    }
                                });

                                final Handler textViewHandler = new Handler();
                                new AsyncTask<Void, Void, Void>(){

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        TranslateOptions options = TranslateOptions.newBuilder()
                                                .setApiKey(GOOGLE_API_KEY)
                                                .build();
                                        com.google.cloud.translate.Translate translate = options.getService();
                                        final Translation translation =
                                                translate.translate(edJapan.getText().toString(),
                                                        Translate.TranslateOption.targetLanguage("id"));
                                        textViewHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                edIndo.setText(translation.getTranslatedText());
                                            }
                                        });
                                        return null;
                                    }
                                }.execute();

                            }
                        })
                        .setOnPickCancel(new IPickCancel() {
                            @Override
                            public void onCancelClick() {
                                //TODO: do what you have to if user clicked cancel
                                Snackbar.make(view, " Cancelled", Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                            }
                        }).show(getActivity().getSupportFragmentManager());
            }
        });



        Picasso.get()
                .load("http://i.imgur.com/DvpvklR.png")
                .placeholder(R.drawable.img_placeholder)
                .into(imageview);

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity())
                .setActionBarTitle("Scan Character");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edJapan = (EditText)getView().findViewById(R.id.edJapan);
        edIndo = (EditText)getView().findViewById(R.id.edIndo);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
