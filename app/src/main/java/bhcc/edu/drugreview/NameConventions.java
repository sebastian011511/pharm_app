package bhcc.edu.drugreview;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * @author nils
 * created 9/21/2017.
 * This class is just examples of how to name things
 */

public class NameConventions
{
    // widgets
    Button btnName;
    EditText etName;
    TextView tvName;
    CheckBox chkName;
    RadioButton rbName;
    ToggleButton tbName;
    Spinner spnName;

    // inner classes
    class FalseBtnHandler implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            // Code
        }
    }
}
