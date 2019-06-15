package org.kernelab.tentacle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.kernelab.basis.Tools;
import org.kernelab.tentacle.core.Coordinate;
import org.kernelab.tentacle.core.Region;
import org.kernelab.tentacle.plane.PlaneCoordinates;
import org.kernelab.tentacle.plane.PlaneImageGuider;
import org.kernelab.tentacle.plane.PlanePathPlanner;
import org.kernelab.tentacle.plane.PlaneRoutePlanner;
import org.kernelab.tentacle.semi.AbstractGuider;
import org.kernelab.tentacle.semi.AbstractPlanner;

public class AppTentacle
{
	public static void main(String[] args)
	{
		JFileChooser fc = new JFileChooser(".");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);

		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			AppTentacle app = new AppTentacle();

			try
			{
				app.loadImage(fc.getSelectedFile());

				app.show();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private BufferedImage			image;

	private JFrame					frame;

	private JPanel					panel;

	private PlaneCoordinates		coordinateSystem;

	private AbstractGuider			guider;

	private AbstractPlanner			planner;

	private LinkedList<Coordinate>	path;

	private LinkedList<Coordinate>	route;

	private Coordinate				startingPoint;

	private Coordinate				terminalPoint;

	private Set<Coordinate>			points;

	private int						pointRadius			= 3;

	private Color					reachableColor		= Color.WHITE;

	private Color					pathColor			= Color.RED;

	private Color					startingPointColor	= Color.GREEN;

	private Color					terminalPointColor	= Color.BLUE;

	private Color					routeColor			= Color.ORANGE;

	private Color					pointsColor			= new Color(150, 0, 255);

	public AppTentacle()
	{
		this.init();
	}

	protected void addPoint(Coordinate point)
	{
		if (!this.isPlanning())
		{
			this.getPoints().add(point);
		}
	}

	protected void arrange()
	{
		this.getFrame().add(this.getPanel());
		this.getFrame().pack();
	}

	protected void calculatePath()
	{
		if (this.getGuider() != null && this.getStartingPoint() != null && this.getTerminalPoint() != null
				&& !this.isPlanning())
		{
			Collection<Coordinate> points = new LinkedList<Coordinate>();
			points.add(this.getStartingPoint());
			points.add(this.getTerminalPoint());

			LinkedList<Coordinate> path = new LinkedList<Coordinate>();
			this.setPath(path);

			PlanePathPlanner planner = new PlanePathPlanner(this);
			planner.setResult(path);
			planner.setPoints(points);
			this.setPlanner(planner);

			new Thread(this.getPlanner()).start();
		}
		this.repaint();
	}

	protected void calculateRoute()
	{
		if (this.getGuider() != null && this.getPoints().size() > 1 && !this.isPlanning())
		{
			LinkedList<Coordinate> route = new LinkedList<Coordinate>();
			this.setRoute(route);

			PlaneRoutePlanner planner = new PlaneRoutePlanner(this);
			planner.setResult(route);
			planner.setPoints(this.getPoints());
			this.setPlanner(planner);

			new Thread(this.getPlanner()).start();
		}
		this.repaint();
	}

	protected void config()
	{
		this.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getPanel().setBackground(Color.GRAY);

		this.getPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				switch (e.getButton())
				{
					case MouseEvent.BUTTON1:
						AppTentacle.this.setStartingPoint(
								AppTentacle.this.getCoordinateSystem().getCoordinate(e.getX(), e.getY()));
						AppTentacle.this.calculatePath();
						break;

					case MouseEvent.BUTTON3:
						AppTentacle.this.setTerminalPoint(
								AppTentacle.this.getCoordinateSystem().getCoordinate(e.getX(), e.getY()));
						AppTentacle.this.calculatePath();
						break;

					case MouseEvent.BUTTON2:
						AppTentacle.this
								.addPoint(AppTentacle.this.getCoordinateSystem().getCoordinate(e.getX(), e.getY()));
						AppTentacle.this.calculateRoute();
						break;
				}
			}
		});
	}

	protected void drawLine(Graphics2D g, Coordinate a, Coordinate b, Color color)
	{
		if (color != null)
		{
			g.setColor(color);
		}
		g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
	}

	protected void drawPoint(Graphics2D g, Coordinate point, int radius, Color color)
	{
		if (color != null)
		{
			g.setColor(color);
		}
		g.fillOval((int) (point.getX() - radius), (int) (point.getY() - radius), radius * 2, radius * 2);
	}

	public PlaneCoordinates getCoordinateSystem()
	{
		return coordinateSystem;
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public AbstractGuider getGuider()
	{
		return guider;
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public JPanel getPanel()
	{
		return panel;
	}

	public LinkedList<Coordinate> getPath()
	{
		return path;
	}

	public Color getPathColor()
	{
		return pathColor;
	}

	public AbstractPlanner getPlanner()
	{
		return planner;
	}

	public int getPointRadius()
	{
		return pointRadius;
	}

	public Set<Coordinate> getPoints()
	{
		return this.points;
	}

	public Color getPointsColor()
	{
		return pointsColor;
	}

	public Color getReachableColor()
	{
		return reachableColor;
	}

	public LinkedList<Coordinate> getRoute()
	{
		return route;
	}

	public Color getRouteColor()
	{
		return routeColor;
	}

	public Coordinate getStartingPoint()
	{
		return startingPoint;
	}

	public Color getStartingPointColor()
	{
		return startingPointColor;
	}

	public Coordinate getTerminalPoint()
	{
		return terminalPoint;
	}

	public Color getTerminalPointColor()
	{
		return terminalPointColor;
	}

	public void hintStatus(String status)
	{
		this.getFrame().setTitle("Tentacle" + (Tools.notNullOrWhite(status) ? " - " : "") + status);
	}

	protected void init()
	{
		this.setPoints(new LinkedHashSet<Coordinate>());
		this.setCoordinateSystem(new PlaneCoordinates());
		this.setFrame(new JFrame("Tentacle"));
		this.setPanel(new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -9194107723491636320L;

			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				AppTentacle.this.paint(g);
			}
		});

		this.config();
		this.arrange();
	}

	public boolean isPlanning()
	{
		return this.getPlanner() != null && this.getPlanner().isRunning();
	}

	public AppTentacle loadImage(File file) throws IOException
	{
		this.setImage(ImageIO.read(file));
		this.setGuider(new PlaneImageGuider(this.getCoordinateSystem(), this.getImage(), Color.white));
		this.getPanel().setPreferredSize(new Dimension(this.getImage().getWidth(), this.getImage().getHeight()));
		this.getFrame().pack();
		return this;
	}

	protected void paint(Graphics g)
	{
		Graphics2D g2d = paintConfig(g);

		paintImage(g2d);

		paintRegions(g2d);

		paintPath(g2d);
		paintPoint(g2d);

		paintRoute(g2d);
		paintPoints(g2d);
	}

	protected Graphics2D paintConfig(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g2d;
	}

	protected void paintImage(Graphics2D g)
	{
		Image img = this.getImage();

		if (img != null)
		{
			g.drawImage(img, 0, 0, this.getPanel());
		}
	}

	protected void paintPath(Graphics2D g)
	{
		List<Coordinate> path = this.getPath();

		if (path != null)
		{
			g.setColor(this.getPathColor());

			int radius = 2;

			Coordinate last = null;

			for (Coordinate point : path)
			{
				if (last != null)
				{
					this.drawLine(g, last, point, null);
				}

				this.drawPoint(g, point, radius, null);

				last = point;
			}
		}
	}

	protected void paintPoint(Graphics2D g)
	{
		int r = this.getPointRadius();

		Coordinate s = this.getStartingPoint();
		if (s != null)
		{
			this.drawPoint(g, s, r, this.getStartingPointColor());
		}

		Coordinate t = this.getTerminalPoint();
		if (t != null)
		{
			this.drawPoint(g, t, r, this.getTerminalPointColor());
		}
	}

	protected void paintPoints(Graphics2D g)
	{
		int r = this.getPointRadius();

		for (Coordinate p : this.getPoints())
		{
			this.drawPoint(g, p, r, this.getPointsColor());
		}
	}

	protected void paintRegions(Graphics2D g)
	{
		if (this.getPlanner() instanceof PlanePathPlanner)
		{
			Set<Region> banned = ((PlanePathPlanner) this.getPlanner()).bannedRegions;
			if (banned != null)
			{
				g.setColor(Color.lightGray);
				for (Region ban : banned)
				{
					ban.draw(g);
				}
			}
		}
	}

	protected void paintRoute(Graphics2D g)
	{
		List<Coordinate> route = this.getRoute();

		if (route != null)
		{
			g.setColor(this.getRouteColor());

			int radius = 2;

			Coordinate last = null;

			for (Coordinate point : route)
			{
				if (last != null)
				{
					this.drawLine(g, last, point, null);
				}

				this.drawPoint(g, point, radius, null);

				last = point;
			}
		}
	}

	public void repaint()
	{
		this.getPanel().repaint();
	}

	protected void setCoordinateSystem(PlaneCoordinates system)
	{
		this.coordinateSystem = system;
	}

	protected void setFrame(JFrame frame)
	{
		this.frame = frame;
	}

	protected void setGuider(AbstractGuider guider)
	{
		this.guider = guider;
	}

	protected void setImage(BufferedImage image)
	{
		this.image = image;
	}

	protected void setPanel(JPanel panel)
	{
		this.panel = panel;
	}

	protected void setPath(LinkedList<Coordinate> path)
	{
		this.path = path;
	}

	public void setPathColor(Color pathColor)
	{
		this.pathColor = pathColor;
	}

	protected void setPlanner(AbstractPlanner planner)
	{
		this.planner = planner;
	}

	public void setPointRadius(int pointRadius)
	{
		this.pointRadius = pointRadius;
	}

	protected void setPoints(Set<Coordinate> points)
	{
		this.points = points;
	}

	public void setPointsColor(Color pointsColor)
	{
		this.pointsColor = pointsColor;
	}

	public void setReachableColor(Color reachableColor)
	{
		this.reachableColor = reachableColor;
	}

	protected void setRoute(LinkedList<Coordinate> route)
	{
		this.route = route;
	}

	public void setRouteColor(Color routeColor)
	{
		this.routeColor = routeColor;
	}

	protected void setStartingPoint(Coordinate startingPoint)
	{
		if (!this.isPlanning())
		{
			this.startingPoint = startingPoint;
		}
	}

	public void setStartingPointColor(Color startingPointColor)
	{
		this.startingPointColor = startingPointColor;
	}

	protected void setTerminalPoint(Coordinate terminalPoint)
	{
		if (!this.isPlanning())
		{
			this.terminalPoint = terminalPoint;
		}
	}

	public void setTerminalPointColor(Color terminalPointColor)
	{
		this.terminalPointColor = terminalPointColor;
	}

	public void show()
	{
		this.getFrame().setVisible(true);
	}
}
