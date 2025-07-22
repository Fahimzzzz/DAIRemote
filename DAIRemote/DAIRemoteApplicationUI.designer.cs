namespace DAIRemote
{
    partial class DAIRemoteApplicationUI
    {
       
        private System.ComponentModel.IContainer components = null;

        
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code


        private void InitializeComponent()
        {
            this.BtnSaveDisplayConfig = new Button();
            this.checkBoxStartup = new CheckBox();
            this.BtnCycleAudioOutputs = new Button();
            this.DisplayLoadProfilesLayout = new FlowLayoutPanel();
            this.DAIRemoteLogo = new PictureBox();
            this.LogoName = new TextBox();
            this.LoadProfile = new TextBox();
            this.AudioCycleHotkey = new Button();
            this.DisplayProfileHotkey = new Button();
            this.AudioComboBox = new ComboBox();
            ((System.ComponentModel.ISupportInitialize)this.DAIRemoteLogo).BeginInit();
            this.SuspendLayout();
            // 
            // BtnSaveDisplayConfig
            // 
            this.BtnSaveDisplayConfig.AccessibleName = "BtnSaveDisplayConfig";
            this.BtnSaveDisplayConfig.BackColor = Color.LightSkyBlue;
<<<<<<< HEAD
            this.BtnSaveDisplayConfig.Location = new Point(12, 97);
=======
            this.BtnSaveDisplayConfig.Location = new Point(12, 137);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.BtnSaveDisplayConfig.Name = "BtnSaveDisplayConfig";
            this.BtnSaveDisplayConfig.Size = new Size(200, 40);
            this.BtnSaveDisplayConfig.TabIndex = 0;
            this.BtnSaveDisplayConfig.Text = "Add Display Profile";
            this.BtnSaveDisplayConfig.UseVisualStyleBackColor = false;
            this.BtnSaveDisplayConfig.Click += this.BtnAddDisplayConfig_Click;
            // 
            // checkBoxStartup
            // 
            this.checkBoxStartup.AutoSize = true;
            this.checkBoxStartup.ForeColor = SystemColors.Control;
<<<<<<< HEAD
            this.checkBoxStartup.Location = new Point(12, 284);
=======
            this.checkBoxStartup.Location = new Point(12, 324);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.checkBoxStartup.Name = "checkBoxStartup";
            this.checkBoxStartup.Size = new Size(184, 19);
            this.checkBoxStartup.TabIndex = 4;
            this.checkBoxStartup.Text = "Launch application on startup";
            this.checkBoxStartup.UseVisualStyleBackColor = true;
            this.checkBoxStartup.CheckedChanged += this.CheckBoxStartup_CheckedChanged;
            // 
            // BtnCycleAudioOutputs
            // 
            this.BtnCycleAudioOutputs.AccessibleName = "BtnCycleAudioOutputs";
            this.BtnCycleAudioOutputs.BackColor = Color.LightSkyBlue;
<<<<<<< HEAD
            this.BtnCycleAudioOutputs.Location = new Point(12, 143);
=======
            this.BtnCycleAudioOutputs.Location = new Point(12, 183);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.BtnCycleAudioOutputs.Name = "BtnCycleAudioOutputs";
            this.BtnCycleAudioOutputs.Size = new Size(200, 40);
            this.BtnCycleAudioOutputs.TabIndex = 5;
            this.BtnCycleAudioOutputs.Text = "Cycle Audio Devices";
            this.BtnCycleAudioOutputs.UseVisualStyleBackColor = false;
            this.BtnCycleAudioOutputs.Click += this.BtnCycleAudioOutputs_Click;
            // 
            // DisplayLoadProfilesLayout
            // 
            this.DisplayLoadProfilesLayout.AutoScroll = true;
            this.DisplayLoadProfilesLayout.Location = new Point(283, 32);
            this.DisplayLoadProfilesLayout.Name = "DisplayLoadProfilesLayout";
            this.DisplayLoadProfilesLayout.Size = new Size(336, 337);
            this.DisplayLoadProfilesLayout.TabIndex = 6;
            // 
            // DAIRemoteLogo
            // 
            this.DAIRemoteLogo.Image = Properties.Resources.DAIRemoteLogo;
            this.DAIRemoteLogo.Location = new Point(62, 11);
            this.DAIRemoteLogo.Margin = new Padding(3, 2, 3, 2);
            this.DAIRemoteLogo.Name = "DAIRemoteLogo";
            this.DAIRemoteLogo.Size = new Size(98, 92);
            this.DAIRemoteLogo.SizeMode = PictureBoxSizeMode.StretchImage;
            this.DAIRemoteLogo.TabIndex = 8;
            this.DAIRemoteLogo.TabStop = false;
            // 
            // LogoName
            // 
            this.LogoName.BackColor = Color.FromArgb(50, 50, 50);
            this.LogoName.BorderStyle = BorderStyle.None;
            this.LogoName.Font = new Font("Cascadia Code", 20F, FontStyle.Bold);
            this.LogoName.ForeColor = Color.White;
            this.LogoName.Location = new Point(37, 96);
            this.LogoName.Margin = new Padding(3, 2, 3, 2);
            this.LogoName.Name = "LogoName";
            this.LogoName.ReadOnly = true;
            this.LogoName.Size = new Size(150, 31);
            this.LogoName.TabIndex = 9;
            this.LogoName.Text = "DAIRemote";
            // 
            // LoadProfile
            // 
            this.LoadProfile.BackColor = Color.FromArgb(50, 50, 50);
            this.LoadProfile.BorderStyle = BorderStyle.None;
            this.LoadProfile.Font = new Font("Segoe UI", 9F, FontStyle.Underline, GraphicsUnit.Point, 0);
            this.LoadProfile.ForeColor = Color.LightSkyBlue;
            this.LoadProfile.Location = new Point(388, 11);
            this.LoadProfile.Margin = new Padding(3, 2, 3, 2);
            this.LoadProfile.Name = "LoadProfile";
            this.LoadProfile.ReadOnly = true;
            this.LoadProfile.Size = new Size(109, 16);
            this.LoadProfile.TabIndex = 11;
            this.LoadProfile.Text = "Current Profiles";
            this.LoadProfile.TextAlign = HorizontalAlignment.Center;
            // 
            // AudioCycleHotkey
            // 
            this.AudioCycleHotkey.AccessibleName = "BtnSetAudioCycleHotkey";
            this.AudioCycleHotkey.BackColor = Color.LightSkyBlue;
<<<<<<< HEAD
            this.AudioCycleHotkey.Location = new Point(12, 189);
=======
            this.AudioCycleHotkey.Location = new Point(12, 229);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.AudioCycleHotkey.Name = "AudioCycleHotkey";
            this.AudioCycleHotkey.Size = new Size(200, 40);
            this.AudioCycleHotkey.TabIndex = 13;
            this.AudioCycleHotkey.Text = "Set Audio Cycle Hotkey";
            this.AudioCycleHotkey.UseVisualStyleBackColor = false;
            this.AudioCycleHotkey.Click += this.BtnSetAudioHotkey_Click;
            // 
            // DisplayProfileHotkey
            // 
            this.DisplayProfileHotkey.AccessibleName = "BtnSetDisplayProfileHotkey";
            this.DisplayProfileHotkey.BackColor = Color.LightSkyBlue;
<<<<<<< HEAD
            this.DisplayProfileHotkey.Location = new Point(12, 235);
=======
            this.DisplayProfileHotkey.Location = new Point(12, 275);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.DisplayProfileHotkey.Name = "DisplayProfileHotkey";
            this.DisplayProfileHotkey.Size = new Size(200, 40);
            this.DisplayProfileHotkey.TabIndex = 14;
            this.DisplayProfileHotkey.Text = "Set Display Profile Hotkeys";
            this.DisplayProfileHotkey.UseVisualStyleBackColor = false;
            this.DisplayProfileHotkey.Click += this.BtnSetDisplayProfileHotkey_click;
            // 
            // AudioComboBox
            // 
            this.AudioComboBox.DropDownStyle = ComboBoxStyle.DropDownList;
            this.AudioComboBox.FormattingEnabled = true;
<<<<<<< HEAD
            this.AudioComboBox.Location = new Point(12, 306);
=======
            this.AudioComboBox.Location = new Point(12, 346);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.AudioComboBox.Name = "AudioComboBox";
            this.AudioComboBox.Size = new Size(257, 23);
            this.AudioComboBox.TabIndex = 15;
            this.AudioComboBox.SelectedIndexChanged += this.AudioComboBox_SelectedIndexChanged;
            // 
            // DAIRemoteApplicationUI
            // 
            this.AutoScaleDimensions = new SizeF(7F, 15F);
            this.AutoScaleMode = AutoScaleMode.Font;
            this.BackColor = Color.FromArgb(50, 50, 50);
<<<<<<< HEAD
            this.ClientSize = new Size(631, 398);
=======
            this.ClientSize = new Size(631, 377);
>>>>>>> 74be7c1 (Implemented showing and hiding form upon each left mouse button double click on the tray icon.)
            this.Controls.Add(this.AudioComboBox);
            this.Controls.Add(this.DisplayProfileHotkey);
            this.Controls.Add(this.AudioCycleHotkey);
            this.Controls.Add(this.LoadProfile);
            this.Controls.Add(this.LogoName);
            this.Controls.Add(this.DAIRemoteLogo);
            this.Controls.Add(this.DisplayLoadProfilesLayout);
            this.Controls.Add(this.BtnCycleAudioOutputs);
            this.Controls.Add(this.checkBoxStartup);
            this.Controls.Add(this.BtnSaveDisplayConfig);
            this.Name = "DAIRemoteApplicationUI";
            this.Text = "DAIRemote";
            Resize += this.DAIRemoteApplicationUI_Resize;
            ((System.ComponentModel.ISupportInitialize)this.DAIRemoteLogo).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();
        }

        #endregion

        private Button BtnSaveDisplayConfig;
        private CheckBox checkBoxStartup;
        private Button BtnCycleAudioOutputs;
        private FlowLayoutPanel DisplayLoadProfilesLayout;
        private PictureBox DAIRemoteLogo;
        private TextBox LogoName;
        private TextBox LoadProfile;
        private Button AudioCycleHotkey;
        private Button DisplayProfileHotkey;
        private ComboBox AudioComboBox;
    }
}