package phonesaver.su;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    TextView tvText;
    TextView tvvText;
    TextView tvvvText;
    SeekBar load;
    String[] type = new String[20];
    Float[] temp = new Float[20];
    Thread thread;
    double power;
    long duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readSensorsType();
//        readTemps();

        load = findViewById(R.id.load);
        tvvvText = findViewById(R.id.tvvvText);
        load.setOnSeekBarChangeListener(this);
        power = 0.5;
        duration = 2000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                tvText.append("Пришло)\n"); TODO:
            }
        }, 1000, 1000);
    }

    public void startHeatBot(View v) {
        tvText.append("Пошло...\n");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                Load.main();
                Load.BusyThread busyThread = new Load.BusyThread("qq", power , duration);
                busyThread.run();
            }
        };
        thread = new Thread(runnable);
        thread.start();
//        tvText.append("Пришло)\n");
    }

    public void stopHeatBot(View v) {
        Thread.interrupted();
        tvText.append("Остановленно\n");
    }

    public void readTemps() {
        Process process;
        int i;
        tvvText = findViewById(R.id.tvvText);
        tvvText.setText("Прошла успешно)\n");

        for (i = 0; i < 20; i++) {
            try {
                process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone" + i + "/temp");
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line = reader.readLine();
                this.temp[i] = Float.parseFloat(line) / 1000.0f;

                if (this.temp[i] != 0.0f) {
                    tvvText.append(String.valueOf(this.temp[i]) + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void readSensorsType() {
        Process process;
        int i;
        tvText = findViewById(R.id.tvText);
        tvText.setText("Проверка... \n");

        for (i = 0; i < 20; i++) {
            try {
                process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone" + i + "/type");
                process.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                this.type[i] = reader.readLine();

                if (this.type[i] != null) {
                    tvText.append(this.type[i] + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tvvvText.setText(String.valueOf(load.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        tvvvText.setText(String.valueOf(load.getProgress()));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        tvvvText.setText(String.valueOf(load.getProgress()));
        power = load.getProgress();
        power = power / 100;
    }
}