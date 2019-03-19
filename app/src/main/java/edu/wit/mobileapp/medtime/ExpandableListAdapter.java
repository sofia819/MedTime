package edu.wit.mobileapp.medtime;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private String logTag = "myApp";
    private String[] timeOfDay = {"Morning", "Afternoon", "Evening", "Night"};
    private ArrayList<PillTime>[] events = new ArrayList[timeOfDay.length];
    private ArrayList<PillTime> schedule;
    private Context context;
    private LayoutInflater inflater;

    public ExpandableListAdapter(Context c, ArrayList<PillTime> sche){
        context = c;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        schedule = sche;

        sortEvents();
    }

    public void sortEvents(){
        for(int i = 0; i < events.length; i++){
            events[i] = new ArrayList<>();
        }

        for(int i = 0; i < schedule.size(); i++){
            for(int j = 0; j < timeOfDay.length; j++){
                if(schedule.get(i).portionOfDay().equals(timeOfDay[j])){
                    events[j].add(schedule.get(i));
                    break;
                }
            }
        }
    }
    @Override
    public int getGroupCount() {
        return timeOfDay.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return events[i].size();
    }

    @Override
    public Object getGroup(int groupPos) {
        return timeOfDay[groupPos];
    }

    @Override
    public Object getChild(int groupPos, int childPos) {
        return events[groupPos].get(childPos);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int groupPos, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPos, boolean b, View view, ViewGroup viewGroup) {
        LinearLayout layout = new LinearLayout(context);

        TextView title = new TextView(context);
        title.setText(timeOfDay[groupPos]);
        title.setTextSize(30);
        layout.addView(title);
        layout.setPadding(80, 0, 0, 0);

        return layout;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
        LinearLayout layout = new LinearLayout(context);

        TextView time = new TextView(context);
        time.setTextSize(22);
        time.setText(events[groupPos].get(childPos).getTimeStamp());
        layout.addView(time);

        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView pillText;
        ArrayList<Pill> p = events[groupPos].get(childPos).getPills();
        for(int i = 0; i < p.size(); i++){
            pillText = new TextView(context);
            pillText.setTextSize(16);
            pillText.setText(p.get(i).getName());
            pillText.setPadding(120, 0, 0, 0);
            layout.addView(pillText);
        }

        layout.setPadding(100, 0, 0, 0);

        return layout;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public boolean shouldExpand(int groupID) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String portionOfDay = new PillTime(hour, minute).portionOfDay();
        return portionOfDay.equals(timeOfDay[groupID]);
    }
}
