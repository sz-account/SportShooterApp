package com.example.dsp.ui.gallery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.dsp.MainActivity;
import com.example.dsp.R;
import com.example.dsp.databinding.FragmentGalleryBinding;
import com.example.dsp.trainingData.AppDatabase;
import com.example.dsp.trainingData.TrainingWithHits;
import com.example.dsp.trainingData.enums.TargetSize;
import com.example.dsp.trainingData.enums.TrainingModeEnum;
import com.google.android.material.chip.ChipGroup;
import com.patrykandpatrick.vico.core.DefaultDimens;
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine;
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent;
import com.patrykandpatrick.vico.core.component.text.TextComponent;
import com.patrykandpatrick.vico.core.entry.ChartEntryModel;
import com.patrykandpatrick.vico.core.entry.EntryListExtensionsKt;
import com.patrykandpatrick.vico.core.entry.FloatEntry;
import com.patrykandpatrick.vico.views.chart.ChartView;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ChartView chartView;
    private AppDatabase instance;

    private TargetSize targetSize;
    private TrainingModeEnum trainingModeEnum;
    private int targetCount = 0;
    private int distance = 0;

    @SuppressLint("NonConstantResourceId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        instance = AppDatabase.getInstance(getActivity());
        
        chartView = root.findViewById(R.id.chart_view);

        ChipGroup chipGroupSize = root.findViewById(R.id.chipGroupSize);
        chipGroupSize.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.Large:
                    targetSize = TargetSize.Large;
                    break;
                case R.id.Medium:
                    targetSize = TargetSize.Medium;
                    break;
                case R.id.Small:
                    targetSize = TargetSize.Small;
                    break;
            }

            UpdateChart();
        });

        ChipGroup chipGroupTrainingMode = root.findViewById(R.id.chipGroupTrainingMode);
        chipGroupTrainingMode.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.Reaction:
                    if (targetCount > 1)
                        trainingModeEnum = TrainingModeEnum.Reflex;
                    else
                        trainingModeEnum = TrainingModeEnum.ReflexSingle;
                    break;
                case R.id.Line:
                    trainingModeEnum = TrainingModeEnum.Line;
                    break;
                case R.id.FiveShoot:
                    trainingModeEnum = TrainingModeEnum.FiveShoot;
                    break;
            }

            UpdateChart();
        });

        ChipGroup chipGroupTargetCount = root.findViewById(R.id.chipGroupTargetCount);
        chipGroupTargetCount.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.chip01:
                    targetCount = 1;
                    break;
                case R.id.chip02:
                    targetCount = 2;
                    break;
                case R.id.chip03:
                    targetCount = 3;
                    break;
                case R.id.chip04:
                    targetCount = 4;
                    break;
                case R.id.chip05:
                    targetCount = 5;
                    break;
            }

            UpdateChart();
        });

        ChipGroup chipGroupTargetDistance = root.findViewById(R.id.chip_groupTargetDistance);
        chipGroupTargetDistance.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.chip10:
                    distance = 10;
                    break;
                case R.id.chip15:
                    distance = 15;
                    break;
                case R.id.chip20:
                    distance = 20;
                    break;
                case R.id.chip25:
                    distance = 25;
                    break;
            }

            UpdateChart();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void UpdateChart()
    {
        if (targetSize != null && trainingModeEnum != null && distance != 0 && targetCount != 0)
        {
            List<TrainingWithHits> byColumnName = instance.trainingWithHitsDao().getByType(targetSize,distance,targetCount,trainingModeEnum,MainActivity.account.getId());

            if (byColumnName.size() > 0) {
                List<FloatEntry> times = new LinkedList<>();
                int i = 0;
                float average = 0;

                for (TrainingWithHits v : byColumnName) {
                    Long time = v.hits.get(v.hits.size() - 1).time;
                    average += time;
                    float sec = (float) time / 1000.0f;
                    times.add(new FloatEntry(i, sec));
                    i++;
                }
                average = average/i;
                average = average / 1000.0f;

                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                String label = new String(decimalFormat.format(average) + " sec");
                TextComponent textComponent = new TextComponent.Builder().build();

                ShapeComponent shapeComponent = new ShapeComponent();
                ThresholdLine thresholdLine = new ThresholdLine(average, label, shapeComponent, DefaultDimens.THRESHOLD_LINE_THICKNESS,
                        textComponent, ThresholdLine.LabelHorizontalPosition.Start, ThresholdLine.LabelVerticalPosition.Top, 0F);
                ChartEntryModel chartEntryModel = EntryListExtensionsKt.entryModelOf(times);
                chartView.setModel(chartEntryModel);
                chartView.getChart().addDecoration(thresholdLine);
            }
        }
    }
}
