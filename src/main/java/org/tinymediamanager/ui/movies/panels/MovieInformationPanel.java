package org.tinymediamanager.ui.movies.panels;

import static org.tinymediamanager.core.Constants.FANART;
import static org.tinymediamanager.core.Constants.MEDIA_FILES;
import static org.tinymediamanager.core.Constants.POSTER;

import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.MediaFileType;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.core.movie.entities.Movie;
import org.tinymediamanager.ui.ColumnLayout;
import org.tinymediamanager.ui.TmmFontHelper;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.ImageLabel;
import org.tinymediamanager.ui.components.LinkLabel;
import org.tinymediamanager.ui.components.StarRater;
import org.tinymediamanager.ui.converter.VoteCountConverter;
import org.tinymediamanager.ui.converter.ZeroIdConverter;
import org.tinymediamanager.ui.movies.MovieSelectionModel;
import org.tinymediamanager.ui.panels.MediaInformationLogosPanel;

import net.miginfocom.swing.MigLayout;

/*
 * Copyright 2012 - 2013 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The Class MovieInformationPanel.
 * 
 * @author Manuel Laggner
 */
public class MovieInformationPanel extends JPanel {
  private static final Logger         LOGGER           = LoggerFactory.getLogger(MovieInformationPanel.class);
  private static final long           serialVersionUID = -8527284262749511617L;
  /** @wbp.nls.resourceBundle messages */
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  private MovieSelectionModel         movieSelectionModel;
  private final ImageIcon             imageEmtpy       = new ImageIcon();
  private ImageIcon                   imageUnwatched;

  private StarRater                   starRater;
  private JLabel                      lblMovieName;
  private JLabel                      lblRating;
  private JLabel                      lblVoteCount;
  private JLabel                      lblTagline;
  private JLabel                      lblYear;
  private LinkLabel                   lblImdbid;
  private JLabel                      lblRunningTime;
  private LinkLabel                   lblTmdbid;
  private JLabel                      lblGenres;
  private JTextPane                   tpPlot;
  private ImageLabel                  lblMoviePoster;
  private JLabel                      lblPosterSize;
  private ImageLabel                  lblMovieFanart;
  private JLabel                      lblFanartSize;
  private JLabel                      lblCertification;
  private LinkLabel                   lblTraktId;

  private MediaInformationLogosPanel  panelLogos;

  /**
   * Instantiates a new movie information panel.
   * 
   * @param movieSelectionModel
   *          the movie selection model
   */
  public MovieInformationPanel(MovieSelectionModel movieSelectionModel) {
    this.movieSelectionModel = movieSelectionModel;

    try {
      imageUnwatched = new ImageIcon(MovieInformationPanel.class.getResource("/org/tinymediamanager/ui/images/unwatched.png"));
    }
    catch (Exception e) {
      imageUnwatched = imageEmtpy;
    }

    initComponents();

    // beansbinding init
    initDataBindings();

    // manual coded binding
    PropertyChangeListener propertyChangeListener = propertyChangeEvent -> {
      String property = propertyChangeEvent.getPropertyName();
      Object source = propertyChangeEvent.getSource();
      // react on selection of a movie and change of a movie
      if (source instanceof MovieSelectionModel || (source instanceof Movie && MEDIA_FILES.equals(property))) {
        Movie movie = null;
        if (source instanceof MovieSelectionModel) {
          movie = ((MovieSelectionModel) source).getSelectedMovie();
        }
        if (source instanceof Movie) {
          movie = (Movie) source;
        }

        if (movie != null) {
          setPoster(movie);
          setFanart(movie);
          panelLogos.setMediaInformationSource(movie);

          if (movie.isWatched()) {
            // lblUnwatched.setIcon(imageEmtpy);
          }
          else {
            // lblUnwatched.setIcon(imageUnwatched);
          }
        }
      }
      if ((source.getClass() == Movie.class && FANART.equals(property))) {
        Movie movie = (Movie) source;
        setFanart(movie);
      }
      if ((source.getClass() == Movie.class && POSTER.equals(property))) {
        Movie movie = (Movie) source;
        setPoster(movie);
      }
    };

    movieSelectionModel.addPropertyChangeListener(propertyChangeListener);
  }

  private void initComponents() {
    putClientProperty("class", "roundedPanel");
    setLayout(new MigLayout("", "[100lp:100lp,grow][300lp:300lp,grow 250]", "[][][][][]"));

    {
      JPanel panelLeft = new JPanel();
      panelLeft.setLayout(new ColumnLayout());
      add(panelLeft, "cell 0 0 1 5,grow");

      lblMoviePoster = new ImageLabel(false, false, true);
      lblMoviePoster.setDesiredAspectRatio(2 / 3f);
      panelLeft.add(lblMoviePoster);

      lblMoviePoster.enableLightbox();
      lblPosterSize = new JLabel(BUNDLE.getString("mediafiletype.poster")); //$NON-NLS-1$
      panelLeft.add(lblPosterSize);

      panelLeft.add(Box.createVerticalStrut(20));

      lblMovieFanart = new ImageLabel(false, false, true);
      lblMovieFanart.setDesiredAspectRatio(16 / 9f);

      panelLeft.add(lblMovieFanart);
      lblMovieFanart.enableLightbox();
      lblFanartSize = new JLabel(BUNDLE.getString("mediafiletype.fanart")); //$NON-NLS-1$
      panelLeft.add(lblFanartSize);
    }
    {
      JPanel panelTopRight = new JPanel();
      add(panelTopRight, "cell 1 0,grow");
      panelTopRight.setLayout(new MigLayout("insets 0 n n n", "[grow]", "[][][][][][][][][][][][]"));

      {
        lblMovieName = new JLabel("");
        panelTopRight.add(lblMovieName, "cell 0 0,grow");
        TmmFontHelper.changeFont(lblMovieName, 1.33, Font.BOLD);
      }
      {
        panelTopRight.add(new JSeparator(), "cell 0 1,growx,aligny center");
      }

      {
        JPanel panelTopDetails = new JPanel();
        panelTopRight.add(panelTopDetails, "cell 0 2,grow");
        panelTopDetails.setLayout(new MigLayout("insets 0", "[][grow][][grow 200]", "[]2lp[]2lp[]2lp[]"));

        {
          JLabel lblYearT = new JLabel(BUNDLE.getString("metatag.year")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblYearT, Font.BOLD);
          panelTopDetails.add(lblYearT, "cell 0 0");

          lblYear = new JLabel("");
          panelTopDetails.add(lblYear, "cell 1 0,growx");
        }

        {
          JLabel lblImdbIdT = new JLabel(BUNDLE.getString("metatag.imdb")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblImdbIdT, Font.BOLD);
          panelTopDetails.add(lblImdbIdT, "cell 2 0");

          lblImdbid = new LinkLabel("");
          lblImdbid.addActionListener(arg0 -> {
            String url = "http://www.imdb.com/title/" + lblImdbid.getNormalText();
            try {
              TmmUIHelper.browseUrl(url);
            }
            catch (Exception e) {
              LOGGER.error("browse to imdbid", e);
              MessageManager.instance
                  .pushMessage(new Message(Message.MessageLevel.ERROR, url, "message.erroropenurl", new String[] { ":", e.getLocalizedMessage() }));
            }
          });
          panelTopDetails.add(lblImdbid, "cell 3 0,growx");
        }

        {
          JLabel lblCertificationT = new JLabel(BUNDLE.getString("metatag.certification")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblCertificationT, Font.BOLD);
          panelTopDetails.add(lblCertificationT, "cell 0 1");

          lblCertification = new JLabel("");
          panelTopDetails.add(lblCertification, "cell 1 1,growx");
        }

        {
          JLabel lblTmdbIdT = new JLabel(BUNDLE.getString("metatag.tmdb")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblTmdbIdT, Font.BOLD);
          panelTopDetails.add(lblTmdbIdT, "cell 2 1");

          lblTmdbid = new LinkLabel("");
          lblTmdbid.addActionListener(arg0 -> {
            String url = "http://www.themoviedb.org/movie/" + lblTmdbid.getNormalText();
            try {
              TmmUIHelper.browseUrl(url);
            }
            catch (Exception e) {
              LOGGER.error("browse to tmdbid", e);
              MessageManager.instance
                  .pushMessage(new Message(Message.MessageLevel.ERROR, url, "message.erroropenurl", new String[] { ":", e.getLocalizedMessage() }));
            }
          });
          panelTopDetails.add(lblTmdbid, "cell 3 1,growx");
        }

        {
          JLabel lblRunningTimeT = new JLabel(BUNDLE.getString("metatag.runtime")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblRunningTimeT, Font.BOLD);
          panelTopDetails.add(lblRunningTimeT, "cell 0 2");

          lblRunningTime = new JLabel("");
          panelTopDetails.add(lblRunningTime, "cell 1 2,growx");
        }

        {
          JLabel lblTraktIdT = new JLabel(BUNDLE.getString("metatag.trakt")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblTraktIdT, Font.BOLD);
          panelTopDetails.add(lblTraktIdT, "cell 2 2");

          lblTraktId = new LinkLabel("");
          lblTraktId.addActionListener(arg0 -> {
            String url = "https://trakt.tv/movies/" + lblTraktId.getNormalText();
            try {
              TmmUIHelper.browseUrl(url);
            }
            catch (Exception e) {
              LOGGER.error("browse to traktid", e);
              MessageManager.instance
                  .pushMessage(new Message(Message.MessageLevel.ERROR, url, "message.erroropenurl", new String[] { ":", e.getLocalizedMessage() }));
            }
          });
          panelTopDetails.add(lblTraktId, "cell 3 2");
        }

        {
          JLabel lblGenresT = new JLabel(BUNDLE.getString("metatag.genre")); //$NON-NLS-1$
          TmmFontHelper.changeFont(lblGenresT, Font.BOLD);
          panelTopDetails.add(lblGenresT, "cell 0 3");

          lblGenres = new JLabel("");
          panelTopDetails.add(lblGenres, "cell 1 3 3 1,growx");
        }
      }

      {
        panelTopRight.add(new JSeparator(), "cell 0 3,growx,aligny center");
      }

      {
        starRater = new StarRater(10, 1);
        panelTopRight.add(starRater, "flowx,cell 0 4,aligny center");
        starRater.setEnabled(false);

        lblRating = new JLabel("");
        panelTopRight.add(lblRating, "cell 0 4,aligny center");

        lblVoteCount = new JLabel("");
        panelTopRight.add(lblVoteCount, "cell 0 4,aligny center");
      }

      {
        panelTopRight.add(new JSeparator(), "cell 0 5,growx,aligny center");
      }

      {
        panelLogos = new MediaInformationLogosPanel();
        panelTopRight.add(panelLogos, "cell 0 6,alignx left,aligny top");
      }

      {
        panelTopRight.add(new JSeparator(), "cell 0 7,growx,aligny center");
      }

      {
        JLabel lblTaglineT = new JLabel(BUNDLE.getString("metatag.tagline")); //$NON-NLS-1$
        TmmFontHelper.changeFont(lblTaglineT, Font.BOLD);
        panelTopRight.add(lblTaglineT, "cell 0 8,alignx left,aligny top");

        lblTagline = new JLabel();
        panelTopRight.add(lblTagline, "cell 0 9,growx,aligny top");
      }

      {
        JLabel lblPlotT = new JLabel(BUNDLE.getString("metatag.plot")); //$NON-NLS-1$
        TmmFontHelper.changeFont(lblPlotT, Font.BOLD);
        panelTopRight.add(lblPlotT, "cell 0 10,alignx left,aligny top");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        panelTopRight.add(scrollPane, "cell 0 11,grow");

        tpPlot = new JTextPane();
        scrollPane.setViewportView(tpPlot);
        tpPlot.setOpaque(false);
        tpPlot.setFocusable(false);
        tpPlot.setEditable(false);
      }
    }
    {
      add(new JSeparator(), "cell 1 2,growx,aligny center");
    }

    {
      MovieDetailsPanel panelBottomRight = new MovieDetailsPanel(movieSelectionModel);
      add(panelBottomRight, "cell 1 4,grow");
    }
  }

  private void setPoster(Movie movie) {
    lblMoviePoster.clearImage();
    lblMoviePoster.setImagePath(movie.getArtworkFilename(MediaFileType.POSTER));
    Dimension posterSize = movie.getArtworkDimension(MediaFileType.POSTER);
    if (posterSize.width > 0 && posterSize.height > 0) {
      lblPosterSize.setText(BUNDLE.getString("mediafiletype.poster") + " - " + posterSize.width + "x" + posterSize.height); //$NON-NLS-1$
    }
    else {
      lblPosterSize.setText(BUNDLE.getString("mediafiletype.poster")); //$NON-NLS-1$
    }
  }

  private void setFanart(Movie movie) {
    lblMovieFanart.clearImage();
    lblMovieFanart.setImagePath(movie.getArtworkFilename(MediaFileType.FANART));
    Dimension fanartSize = movie.getArtworkDimension(MediaFileType.FANART);
    if (fanartSize.width > 0 && fanartSize.height > 0) {
      lblFanartSize.setText(BUNDLE.getString("mediafiletype.fanart") + " - " + fanartSize.width + "x" + fanartSize.height); //$NON-NLS-1$
    }
    else {
      lblFanartSize.setText(BUNDLE.getString("mediafiletype.fanart")); //$NON-NLS-1$
    }
  }

  protected void initDataBindings() {
    BeanProperty<MovieSelectionModel, Float> movieSelectionModelBeanProperty_1 = BeanProperty.create("selectedMovie.rating");
    BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
    AutoBinding<MovieSelectionModel, Float, JLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_1, lblRating, jLabelBeanProperty);
    autoBinding_1.bind();
    //
    BeanProperty<StarRater, Float> starRaterBeanProperty = BeanProperty.create("rating");
    AutoBinding<MovieSelectionModel, Float, StarRater, Float> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_1, starRater, starRaterBeanProperty);
    autoBinding_3.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_2 = BeanProperty.create("selectedMovie.votes");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_2, lblVoteCount, jLabelBeanProperty);
    autoBinding_2.setConverter(new VoteCountConverter());
    autoBinding_2.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_8 = BeanProperty.create("selectedMovie.year");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_8, lblYear, jLabelBeanProperty);
    autoBinding_9.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_12 = BeanProperty.create("selectedMovie.imdbId");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_12, lblImdbid, jLabelBeanProperty);
    autoBinding_10.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_13 = BeanProperty.create("selectedMovie.runtime");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_13, lblRunningTime, jLabelBeanProperty);
    autoBinding_14.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_15 = BeanProperty.create("selectedMovie.tmdbId");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_15, lblTmdbid, jLabelBeanProperty);
    autoBinding_7.setConverter(new ZeroIdConverter());
    autoBinding_7.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_16 = BeanProperty.create("selectedMovie.genresAsString");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_17 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_16, lblGenres, jLabelBeanProperty);
    autoBinding_17.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_14 = BeanProperty.create("selectedMovie.plot");
    BeanProperty<JTextPane, String> jTextPaneBeanProperty = BeanProperty.create("text");
    AutoBinding<MovieSelectionModel, String, JTextPane, String> autoBinding_18 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_14, tpPlot, jTextPaneBeanProperty);
    autoBinding_18.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_3 = BeanProperty.create("selectedMovie.tagline");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_3, lblTagline, jLabelBeanProperty);
    autoBinding_4.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty_4 = BeanProperty.create("selectedMovie.title");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_4, lblMovieName, jLabelBeanProperty);
    autoBinding_5.bind();
    //
    BeanProperty<MovieSelectionModel, String> movieSelectionModelBeanProperty = BeanProperty.create("selectedMovie.certification.name");
    AutoBinding<MovieSelectionModel, String, JLabel, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty, lblCertification, jLabelBeanProperty);
    autoBinding.bind();
    //
    BeanProperty<MovieSelectionModel, Integer> movieSelectionModelBeanProperty_5 = BeanProperty.create("selectedMovie.traktId");
    AutoBinding<MovieSelectionModel, Integer, JLabel, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ, movieSelectionModel,
        movieSelectionModelBeanProperty_5, lblTraktId, jLabelBeanProperty);
    autoBinding_6.setConverter(new ZeroIdConverter());
    autoBinding_6.bind();
  }
}