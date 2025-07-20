package com.example.dairemote_app;

import static org.junit.jupiter.api.Assertions.*;

import androidx.appcompat.app.AlertDialog;

import com.example.dairemote_app.fragments.ServersFragment;
import com.example.dairemote_app.utils.TutorialMediator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class TutorialMediatorTest {

    TutorialMediator tutorialMediator;
    AlertDialog.Builder mockBuilder;

    @BeforeEach
    void setUp() {
        tutorialMediator = new TutorialMediator();
        mockBuilder = Mockito.mock(AlertDialog.Builder.class);
        TutorialMediator.SetBuilder(mockBuilder);
    }

    @AfterEach
    void tearDown() {
        tutorialMediator = null;
    }

    @Test
    void getInstance() {
        TutorialMediator instance1 = TutorialMediator.GetInstance(mockBuilder);
        TutorialMediator instance2 = TutorialMediator.GetInstance(mockBuilder);

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void getBuilder() {
        AlertDialog.Builder result = tutorialMediator.GetBuilder();
        assertNotNull(result);
        assertSame(mockBuilder, result);

    }


    @Test
    void getTutorialOn() {
        tutorialMediator.tutorialOn = true;
        assertTrue(tutorialMediator.tutorialOn, "Tutorial is on");

        tutorialMediator.tutorialOn = false;
        assertFalse(tutorialMediator.tutorialOn);

    }

    @Test
    void getCurrentStep() {
        tutorialMediator.currentStep = 0;
        assertEquals(0, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 1;
        assertEquals(1, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 2;
        assertEquals(2, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 3;
        assertEquals(3, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 4;
        assertEquals(4, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 5;
        assertEquals(5, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 6;
        assertEquals(6, tutorialMediator.currentStep);

        tutorialMediator.currentStep = 7;
        assertEquals(7, tutorialMediator.currentStep);

    }

    @Test
    void setServersPage() {
        ServersFragment mockServersFragment = Mockito.mock(ServersFragment.class);
        tutorialMediator.setServersPage(mockServersFragment);
        assertNotNull(mockServersFragment);

    }

    @Test
    void checkIfStepCompleted() {
        tutorialMediator.currentStep = 0;
        assertTrue(tutorialMediator.checkIfStepCompleted());

        tutorialMediator.currentStep = 3;
        assertFalse(tutorialMediator.checkIfStepCompleted());

        tutorialMediator.currentStep = 5;
        assertTrue(tutorialMediator.checkIfStepCompleted());

    }


}