using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.IO;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using System.Diagnostics;

namespace CrucifixInjector
{
    public partial class MainForm : Form
    {
        private Process targetProcess;
        private InjectionStatus status;
        private Timer statusCheckTimer;
        private Timer animationTimer;
        private float animationProgress = 0f;
        private int pulsePhase = 0;

        [DllImport("user32.dll")]
        private static extern int SetWindowLong(IntPtr hWnd, int nIndex, int dwNewLong);

        [DllImport("user32.dll")]
        private static extern int GetWindowLong(IntPtr hWnd, int nIndex);

        [DllImport("user32.dll")]
        private static extern IntPtr SetLayeredWindowAttributes(IntPtr hWnd, uint crKey, byte bAlpha, uint dwFlags);

        private const int GWL_EXSTYLE = -20;
        private const int WS_EX_TRANSPARENT = 0x20;
        private const int LWA_COLORKEY = 0x1;
        private const int LWA_ALPHA = 0x2;

        private enum InjectionStatus
        {
            Waiting,
            Detected,
            Injecting,
            Success,
            Error
        }

        public MainForm()
        {
            InitializeComponent();
            InitializeCustomUI();
            status = InjectionStatus.Waiting;
            
            statusCheckTimer = new Timer();
            statusCheckTimer.Interval = 500;
            statusCheckTimer.Tick += StatusCheckTimer_Tick;
            statusCheckTimer.Start();

            animationTimer = new Timer();
            animationTimer.Interval = 16;
            animationTimer.Tick += AnimationTimer_Tick;
            animationTimer.Start();
        }

        private void InitializeCustomUI()
        {
            this.FormBorderStyle = FormBorderStyle.None;
            this.BackColor = Color.FromArgb(21, 21, 25);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.Size = new Size(400, 250);
            this.Region = new Region(CreateRoundedRegion(this.ClientRectangle, 12));

            // Enable double buffering for smooth rendering
            typeof(Control).GetProperty("DoubleBuffered", System.Reflection.BindingFlags.NonPublic | System.Reflection.BindingFlags.Instance)
                .SetValue(this, true, null);
        }

        private GraphicsPath CreateRoundedRegion(Rectangle rect, int radius)
        {
            GraphicsPath path = new GraphicsPath();
            path.AddArc(rect.X, rect.Y, radius * 2, radius * 2, 180, 90);
            path.AddArc(rect.Right - radius * 2, rect.Y, radius * 2, radius * 2, 270, 90);
            path.AddArc(rect.Right - radius * 2, rect.Bottom - radius * 2, radius * 2, radius * 2, 0, 90);
            path.AddArc(rect.X, rect.Bottom - radius * 2, radius * 2, radius * 2, 90, 90);
            path.CloseAllFigures();
            return path;
        }

        private void StatusCheckTimer_Tick(object sender, EventArgs e)
        {
            targetProcess = ProcessDetector.FindMinecraftProcess();
            
            if (targetProcess != null)
            {
                if (status == InjectionStatus.Waiting)
                    status = InjectionStatus.Detected;
            }
            else
            {
                if (status == InjectionStatus.Detected)
                    status = InjectionStatus.Waiting;
            }
            
            this.Invalidate();
        }

        private void AnimationTimer_Tick(object sender, EventArgs e)
        {
            pulsePhase = (pulsePhase + 1) % 60;
            
            if (status == InjectionStatus.Injecting)
            {
                animationProgress = Math.Min(animationProgress + 0.02f, 1f);
            }
            
            this.Invalidate();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            
            Graphics g = e.Graphics;
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.TextRenderingHint = System.Drawing.Text.TextRenderingHint.ClearTypeGridFit;

            // Draw background with gradient
            using (LinearGradientBrush bgBrush = new LinearGradientBrush(
                this.ClientRectangle, 
                Color.FromArgb(21, 21, 25), 
                Color.FromArgb(30, 30, 35), 
                LinearGradientMode.Vertical))
            {
                g.FillRectangle(bgBrush, this.ClientRectangle);
            }

            // Draw border
            using (Pen borderPen = new Pen(Color.FromArgb(212, 160, 160), 1))
            {
                g.DrawPath(borderPen, CreateRoundedRegion(this.ClientRectangle, 12));
            }

            // Draw title with gradient
            string title = "CRUCIFIX.WIN";
            using (Font titleFont = new Font("Segoe UI", 18, FontStyle.Bold))
            {
                Rectangle titleRect = new Rectangle(0, 30, this.Width, 40);
                StringFormat sf = new StringFormat
                {
                    Alignment = StringAlignment.Center,
                    LineAlignment = StringAlignment.Center
                };

                using (LinearGradientBrush titleBrush = new LinearGradientBrush(
                    titleRect,
                    Color.FromArgb(212, 160, 160),
                    Color.FromArgb(180, 100, 100),
                    LinearGradientMode.Horizontal))
                {
                    g.DrawString(title, titleFont, titleBrush, titleRect, sf);
                }
            }

            // Draw status indicator
            Color statusColor = GetStatusColor();
            int pulseSize = (int)(4 + Math.Sin(pulsePhase * 0.1) * 2);
            
            Point statusCenter = new Point(this.Width / 2 - 80, 100);
            using (SolidBrush statusBrush = new SolidBrush(statusColor))
            {
                g.FillEllipse(statusBrush, statusCenter.X - pulseSize, statusCenter.Y - pulseSize, pulseSize * 2, pulseSize * 2);
            }

            // Draw status text
            string statusText = GetStatusText();
            using (Font statusFont = new Font("Segoe UI", 10))
            {
                Rectangle statusRect = new Rectangle(0, 90, this.Width, 30);
                StringFormat sf = new StringFormat
                {
                    Alignment = StringAlignment.Center,
                    LineAlignment = StringAlignment.Center
                };
                g.DrawString(statusText, statusFont, Brushes.White, statusRect, sf);
            }

            // Draw inject button
            Rectangle buttonRect = new Rectangle(this.Width / 2 - 80, 140, 160, 45);
            bool isHovering = buttonRect.Contains(PointToClient(MousePosition));
            
            Color buttonColor = isHovering ? Color.FromArgb(220, 140, 140) : Color.FromArgb(212, 160, 160);
            using (SolidBrush buttonBrush = new SolidBrush(buttonColor))
            {
                g.FillPath(buttonBrush, CreateRoundedRegion(buttonRect, 8));
            }

            // Draw button glow effect
            if (isHovering)
            {
                using (GraphicsPath glowPath = new GraphicsPath())
                {
                    Rectangle glowRect = buttonRect;
                    glowRect.Inflate(10, 10);
                    glowPath.AddEllipse(glowRect);
                    using (PathGradientBrush glowBrush = new PathGradientBrush(glowPath))
                    {
                        glowBrush.CenterColor = Color.FromArgb(30, 212, 160, 160);
                        glowBrush.SurroundColors = new Color[] { Color.Transparent };
                        g.FillPath(glowBrush, glowPath);
                    }
                }
            }

            string buttonText = status == InjectionStatus.Success ? "INJECTED" : "INJECT";
            using (Font buttonFont = new Font("Segoe UI", 11, FontStyle.Bold))
            {
                StringFormat sf = new StringFormat
                {
                    Alignment = StringAlignment.Center,
                    LineAlignment = StringAlignment.Center
                };
                g.DrawString(buttonText, buttonFont, Brushes.White, buttonRect, sf);
            }

            // Draw loading animation if injecting
            if (status == InjectionStatus.Injecting)
            {
                Rectangle loadingRect = new Rectangle(this.Width / 2 - 20, 195, 40, 40);
                for (int i = 0; i < 8; i++)
                {
                    float angle = (i * 45 + pulsePhase * 6) * (float)Math.PI / 180;
                    float radius = 12;
                    float x = loadingRect.X + 20 + (float)Math.Cos(angle) * radius;
                    float y = loadingRect.Y + 20 + (float)Math.Sin(angle) * radius;
                    float alpha = 255 * (1 - (float)i / 8);
                    
                    using (SolidBrush dotBrush = new SolidBrush(Color.FromArgb((int)alpha, 212, 160, 160)))
                    {
                        g.FillEllipse(dotBrush, x - 2, y - 2, 4, 4);
                    }
                }
            }
        }

        private Color GetStatusColor()
        {
            switch (status)
            {
                case InjectionStatus.Waiting:
                    return Color.FromArgb(150, 150, 150);
                case InjectionStatus.Detected:
                    return Color.FromArgb(255, 165, 0);
                case InjectionStatus.Injecting:
                    return Color.FromArgb(212, 160, 160);
                case InjectionStatus.Success:
                    return Color.FromArgb(100, 255, 100);
                case InjectionStatus.Error:
                    return Color.FromArgb(255, 100, 100);
                default:
                    return Color.Gray;
            }
        }

        private string GetStatusText()
        {
            switch (status)
            {
                case InjectionStatus.Waiting:
                    return "Waiting for Minecraft...";
                case InjectionStatus.Detected:
                    string clientName = ProcessDetector.IsLunarClient(targetProcess) ? "Lunar Client" : "Minecraft";
                    return $"{clientName} detected";
                case InjectionStatus.Injecting:
                    return "Injecting...";
                case InjectionStatus.Success:
                    return "Injected successfully";
                case InjectionStatus.Error:
                    return "Injection failed";
                default:
                    return "Unknown status";
            }
        }

        protected override void OnMouseDown(MouseEventArgs e)
        {
            base.OnMouseDown(e);
            
            Rectangle buttonRect = new Rectangle(this.Width / 2 - 80, 140, 160, 45);
            if (buttonRect.Contains(e.Location) && status == InjectionStatus.Detected)
            {
                PerformInjection();
            }
        }

        private void PerformInjection()
        {
            status = InjectionStatus.Injecting;
            this.Invalidate();

            try
            {
                string dllPath = Path.Combine(Application.StartupPath, "CrucifixDLL.dll");
                bool success = Injector.Inject(targetProcess, dllPath);
                
                if (success)
                {
                    status = InjectionStatus.Success;
                }
                else
                {
                    status = InjectionStatus.Error;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"Injection failed: {ex.Message}", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                status = InjectionStatus.Error;
            }
            
            this.Invalidate();
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            this.Invalidate();
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                statusCheckTimer?.Stop();
                statusCheckTimer?.Dispose();
                animationTimer?.Stop();
                animationTimer?.Dispose();
            }
            base.Dispose(disposing);
        }
    }
}
