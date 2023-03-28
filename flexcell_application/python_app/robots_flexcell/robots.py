import numpy as np
import os
import time
from spatialmath import SE3
from spatialmath.base import trnorm
import roboticstoolbox as rtb
from numpy import pi
import random
import csv

# kuka DH parameters
class KukaLBR(rtb.DHRobot):
    def __init__(self):
        d1 = 0.340
        d3 = 0.4
        d5 = 0.4
        d7 = 0.126+0.035
        L1 = rtb.RevoluteMDH(d=d1, a=0, alpha=0)
        L2 = rtb.RevoluteMDH(d=0, a=0, alpha=-pi/2)
        L3 = rtb.RevoluteMDH(d=d3, a=0, alpha=pi/2)
        L4 = rtb.RevoluteMDH(d=0, a=0, alpha=pi/2)
        L5 = rtb.RevoluteMDH(d=d5, a=0, alpha=-pi/2)
        L6 = rtb.RevoluteMDH(d=0, a=0, alpha=-pi/2)
        L7 = rtb.RevoluteMDH(d=d7, a=0, alpha=pi/2)
        super().__init__([L1, L2, L3, L4, L5, L6, L7],name="KUKA")

class UR5e(rtb.DHRobot):
    def __init__(self):
        L1 = rtb.RevoluteMDH(d=0.1625, a=0, alpha=0)
        L2 = rtb.RevoluteMDH(d=0, a=0, alpha=pi/2)
        L3 = rtb.RevoluteMDH(d=0, a=-0.425, alpha=0) # changed a to negative
        L4 = rtb.RevoluteMDH(d=0.1333, a=-0.3922, alpha=0) # changed a to negative
        L5 = rtb.RevoluteMDH(d=0.0997, a=0, alpha=pi/2)
        L6 = rtb.RevoluteMDH(d=0.0996, a=0, alpha=-pi/2)
        #L1 = rtb.RevoluteMDH(d=0.1625, a=0, alpha=pi/2)
        #L2 = rtb.RevoluteMDH(d=0, a=0, alpha=0)
        #L3 = rtb.RevoluteMDH(d=0, a=0.425, alpha=0)
        #L4 = rtb.RevoluteMDH(d=0.1333, a=0.3922, alpha=pi/2)
        #L5 = rtb.RevoluteMDH(d=0.0997, a=0, alpha=-pi/2)
        #L6 = rtb.RevoluteMDH(d=0.0996, a=0, alpha=0)
        super().__init__([L1, L2, L3, L4, L5, L6],name="UR5e")


class KukaLBR_RL(KukaLBR):
    def __init__(self):
        d1 = 0.340
        d3 = 0.4
        d5 = 0.4
        d7 = 0.126+0.035
        L1 = rtb.RevoluteMDH(d=d1, a=0, alpha=0)
        L2 = rtb.RevoluteMDH(d=0, a=0, alpha=-pi/2)
        L3 = rtb.RevoluteMDH(d=d3, a=0, alpha=pi/2)
        L4 = rtb.RevoluteMDH(d=0, a=0, alpha=pi/2)
        L5 = rtb.RevoluteMDH(d=d5, a=0, alpha=-pi/2)
        L6 = rtb.RevoluteMDH(d=0, a=0, alpha=-pi/2)
        L7 = rtb.RevoluteMDH(d=d7, a=0, alpha=pi/2)
        self.prev_q0 = 0
        self.prev_q1 = 0
        self.prev_q2 = 0
        self.prev_q3 = 0
        self.prev_q4 = 0
        self.prev_q5 = 0
        self.prev_q6 = 0
        self.actual_q0 = 0
        self.actual_q1 = 0
        self.actual_q2 = 0
        self.actual_q3 = 0
        self.actual_q4 = 0
        self.actual_q5 = 0
        self.actual_q6 = 0
        self.target_q0 = 0
        self.target_q1 = 0
        self.target_q2 = 0
        self.target_q3 = 0
        self.target_q4 = 0
        self.target_q5 = 0
        self.target_q6 = 0
        self.is_busy = False
        #self.is_idle = True
        super().__init__()

    def compute_ik_num(self,x,y,z,rounded=False):
        T = SE3(trnorm(np.array([[np.cos(-np.pi),0,np.sin(-np.pi),x],[0,1,0,y],[np.sin(-np.pi),0,np.cos(-np.pi),z],[0,0,0,1]]))) # Rotation of pi around the y-axis

        #sol1 = self.ikine_LM(T,q0=[0,0,0,0,0,np.pi/2,0])
        sol1 = self.ikine_LM(T,q0=[0,0,0,-np.pi/2,0,np.pi/2,0])
        if (sol1[1] > 0):
            solution1 = sol1[0]
            if (rounded):
                solution1 = np.round(sol1[0],2)
        else:
            solution1 = np.nan
        sol2 = self.ik_lm_chan(T,q0=[0,0,0,0,0,np.pi,0])
        #sol2 = self.ik_lm_chan(T,q0=[0,0,0,np.pi/2,0,np.pi/2,0])
        if (sol2[1] > 0):
            solution2 = sol2[0]
            if (rounded):
                solution2 = np.round(sol2[0],2)
        else:
            solution2 = np.nan

        return solution1,solution2

    def compute_ik_validity(self,x,y,z,rounded=False):
        T = SE3(trnorm(np.array([[np.cos(-np.pi),0,np.sin(-np.pi),x],[0,1,0,y],[np.sin(-np.pi),0,np.cos(-np.pi),z],[0,0,0,1]]))) # Rotation of pi around the y-axis

        sol1 = self.ikine_LM(T,q0=[0,0,0,-np.pi/2,0,np.pi/2,0])
        sol1_feasible = True
        if (sol1[1] > 0):
            sol1_feasible = True
        else:
            sol1_feasible = False
        sol2 = self.ik_lm_chan(T,q0=[0,0,0,0,0,np.pi,0])
        sol2_feasible = True
        if (sol2[1] > 0):
            sol2_feasible = True
        else:
            sol2_feasible = False

        if (not sol1_feasible and not sol2_feasible):
            return False
        return True

    def compute_xyz_flexcell(self,X,Y,Z=0,rx=0,ry=0,rz=0):
        ## Inversed axis, (x->y,y->x)
        Z_table_level = 0.20
        #Z_table_level = 0.05
        YMIN = 0
        YMAX = 23
        XMIN = 0
        XMAX = 15
        HOLE_DIST = 0.05
        x_min = -0.35
        x_max = 0.6#0.45
        y_min = -0.145
        y_max = 1.055

        #comp_x = x_max - ((X)*HOLE_DIST)
        #comp_y = y_min + ((Y)*HOLE_DIST)
        comp_x = (x_max - ((X)*HOLE_DIST))
        comp_y = -(y_min + ((Y)*HOLE_DIST))
        comp_z = Z_table_level + Z * HOLE_DIST
        rx = rx
        ry = ry
        rz = rz

        return comp_x,comp_y,comp_z

    def move_initial_position(self,n_steps=70):
        prev_q = self.get_actual_position()
        x = 0.35
        y = -0.245
        z = 0.250
        solution1,solution2 = self.compute_ik_num(x,y,z,rounded=False)
        if (solution1 is not np.nan):
            #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="kukamotion1.gif")
            time_used = n_steps * 0.05 * 2
            #plt.pause(0.01)
            solution11,solution21 = self.compute_ik_num(x,y,z,rounded=False)
            if (solution11 is not np.nan):
                qt = rtb.jtraj(solution1, solution11, n_steps)
                #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="kukamotion11.gif")
                time_used = time_used + n_steps * 0.05 * 2
                #plt.pause(0.01)
                return solution11
            else:
                time_used = time_used + n_steps * 0.05 * 2
                return np.array([0,0,0,0,0,0,0])
        else:
            time_used = n_steps * 0.05 * 2
            return np.array([0,0,0,0,0,0,0])

    def move_p(self,x,y,z,n_steps=70):
        solution1,solution2 = self.compute_ik_num(x,y,z,rounded=False)
        if (solution1 is not np.nan):
            if (solution2 is not np.nan):
                time_used = n_steps * 0.05 * 2
                return np.array([solution1,solution2])
            else:
                time_used = n_steps * 0.05 * 2
                return np.array([solution1])
        else:
            if (solution2 is not np.nan):
                time_used = n_steps * 0.05 * 2
                return np.array([solution2])
            else:
                time_used = n_steps * 0.05 * 2
                return np.array([np.array([0,0,0,0,0,0,0])])

    def move_p_with_initial_position(self,x,y,z,n_steps=70):
        prev_q = self.get_actual_position()
        solution1,solution2 = self.compute_ik_num(x,y,z+0.1,rounded=False)
        if (solution1 is not np.nan):
            qt = rtb.jtraj(prev_q, solution1, n_steps)
            #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="kukamotionxyz1.gif")
            time_used = n_steps * 0.05 * 2
            #plt.pause(0.01)
            solution11,solution21 = self.compute_ik_num(x,y,z,rounded=False)
            if (solution11 is not np.nan):
                qt = rtb.jtraj(solution1, solution11, n_steps)
                #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="kukamotionxyz11.gif")
                time_used = time_used + n_steps * 0.05 * 2
                #plt.pause(0.01)
                return solution11
            else:
                time_used = time_used + n_steps * 0.05 * 2
                return np.array([0,0,0,0,0,0,0])
        else:
            time_used = n_steps * 0.05 * 2
            return np.array([0,0,0,0,0,0,0])

    def get_actual_position(self):
        return np.array([self.actual_q0,self.actual_q1,self.actual_q2,
                         self.actual_q3,self.actual_q4,self.actual_q5,self.actual_q6])

    def get_previous_position(self):
        return np.array([self.prev_q0,self.prev_q1,self.prev_q2,
                         self.prev_q3,self.prev_q4,self.prev_q5,self.prev_q6])

    def get_target_position(self):
        return np.array([self.target_q0,self.target_q1,self.target_q2,
                         self.target_q3,self.target_q4,self.target_q5,self.target_q6])

    def set_actual_position(self,array):
        self.actual_q0 = array[0]
        self.actual_q1 = array[1]
        self.actual_q2 = array[2]
        self.actual_q3 = array[3]
        self.actual_q4 = array[4]
        self.actual_q5 = array[5]
        self.actual_q6 = array[6]

    def set_previous_position(self,array):
        self.prev_q0 = array[0]
        self.prev_q1 = array[1]
        self.prev_q2 = array[2]
        self.prev_q3 = array[3]
        self.prev_q4 = array[4]
        self.prev_q5 = array[5]
        self.prev_q6 = array[6]

    def set_target_position(self,array):
        self.target_q0 = array[0]
        self.target_q1 = array[1]
        self.target_q2 = array[2]
        self.target_q3 = array[3]
        self.target_q4 = array[4]
        self.target_q5 = array[5]
        self.target_q6 = array[6]

class UR5e_RL(UR5e):

    def __init__(self):
        L1 = rtb.RevoluteMDH(d=0.1625, a=0, alpha=0)
        L2 = rtb.RevoluteMDH(d=0, a=0, alpha=pi/2)
        L3 = rtb.RevoluteMDH(d=0, a=0.425, alpha=0)
        L4 = rtb.RevoluteMDH(d=0.1333, a=0.3922, alpha=0)
        L5 = rtb.RevoluteMDH(d=0.0997, a=0, alpha=pi/2)
        L6 = rtb.RevoluteMDH(d=0.0996, a=0, alpha=-pi/2)
        self.prev_q0 = 0
        self.prev_q1 = 0
        self.prev_q2 = 0
        self.prev_q3 = 0
        self.prev_q4 = 0
        self.prev_q5 = 0
        self.actual_q0 = 0
        self.actual_q1 = 0
        self.actual_q2 = 0
        self.actual_q3 = 0
        self.actual_q4 = 0
        self.actual_q5 = 0
        self.target_q0 = 0
        self.target_q1 = 0
        self.target_q2 = 0
        self.target_q3 = 0
        self.target_q4 = 0
        self.target_q5 = 0
        self.is_busy = False
        #self.is_idle = True
        super().__init__()

    def compute_fk(self,j1, j2, j3, j4 , j5, j6):
        a2 = 0.425
        a3 = 0.3922
        d1 = 0.1625
        d4 = 0.1333
        d5 = 0.0997
        d6 = 0.0996
        T_0_1 = np.array([[np.cos(j1), -np.sin(j1), 0, 0],[np.sin(j1), np.cos(j1), 0, 0],[0, 0, 1, d1],[0,0,0,1]])
        T_1_2 = np.array([[np.cos(j2), -np.sin(j2), 0, 0],[0, 0, -1, 0],[np.sin(j2), np.cos(j2), 0, 0],[0,0,0,1]])
        T_2_3 = np.array([[np.cos(j3), -np.sin(j3), 0, a2],[np.sin(j3), np.cos(j3), 0, 0],[0,0,1,0],[0,0,0,1]])
        T_3_4 = np.array([[np.cos(j4), -np.sin(j4), 0, a3],[np.sin(j4), np.cos(j4), 0, 0],[0, 0, 1, d4],[0,0,0,1]])
        T_4_5 = np.array([[np.cos(j5), -np.sin(j5), 0, 0],[0,0,-1,-d5],[np.sin(j5), np.cos(j5), 0, 0],[0,0,0,1]])
        T_5_6 = np.array([[np.cos(j6), -np.sin(j6), 0, 0],[0,0,1,d6],[-np.sin(j6),-np.cos(j6), 0,0],[0,0,0,1]])
        T_0_6 = T_0_1 @ T_1_2 @ T_2_3 @ T_3_4 @ T_4_5 @ T_5_6
        return T_0_6

    def compute_ik_num(self,x,y,z,rounded=False):
        #T = SE3(trnorm(np.array([[-1,0,0,x],[0,1,0,y],[0,0,-1,z],[0,0,0,1]])))
        T = SE3(trnorm(np.array([[1,0,0,x],[0,np.cos(5*np.pi/4),-np.sin(5*np.pi/4),y],[0,np.sin(5*np.pi/4),np.cos(5*np.pi/4),z],[0,0,0,1]]))) # Rotation of 5*pi/4 on the x-axis

        #sol = self.ikine_LMS(T,q0=[0,0,0,np.pi,0,0])
        sol = self.ikine_LMS(T,q0=[-np.pi/2,np.pi/4,0,np.pi/2,0,0])
        #sol = self.ikine_LMS(T,q0=[0,-np.pi/2,0,-np.pi/2,0,0])
        if (sol[1] > 0):
            solution = sol[0]
            if (rounded):
                solution = np.round(sol[0],2)
            return solution
        return np.nan

    def compute_ik_validity(self,x,y,z,rounded=False):
        T = SE3(trnorm(np.array([[1,0,0,x],[0,np.cos(5*np.pi/4),-np.sin(5*np.pi/4),y],[0,np.sin(5*np.pi/4),np.cos(5*np.pi/4),z],[0,0,0,1]]))) # Rotation of 5*pi/4 on the x-axis

        sol = self.ikine_LMS(T,q0=[-np.pi/2,np.pi/4,0,np.pi/2,0,0])
        #sol = self.ikine_LMS(T,q0=[0,-np.pi/2,0,-np.pi/2,0,0])
        if (sol[1] > 0):
            return True
        else:
            return False

    def compute_ik_analytic(self,x,y,z):
        a2 = 0.425
        a3 = 0.3922
        d1 = 0.1625
        d4 = 0.1333
        d5 = 0.0997
        d6 = 0.0996
        # desired position and orientation [m]
        T = np.array([[0,0,1,x],[0.5,-0.866,0,y],[0.866,0.5,0,z],[0,0,0,1]])
        #T = SE3(trnorm(T))
        x = T[0][3]
        y = T[1][3]
        z = T[2][3]

        # Joint angles
        P_0_5 = T @ np.array([0,0,-d6,1]).T

        # j1
        r1 = np.sqrt(P_0_5[0]**2 + P_0_5[1]**2)
        phi1 = np.arctan2(P_0_5[1], P_0_5[0])
        phi2 = np.arccos(d4/r1)
        j1 = phi1 - phi2 + pi/2


        # j5
        j5 = np.arccos((x*np.sin(j1)-y*np.cos(j1)-d4)/d6)
        if np.isnan(j5):
            j5 = 0.0

        # j6
        R_0_6 = T[0:3,0:3]
        X_0_6_rot = R_0_6[:,0]
        Y_0_6_rot = R_0_6[:,1]
        X_6_0_rot = -X_0_6_rot
        Y_6_0_rot = -Y_0_6_rot
        num1 = (-Y_6_0_rot[0]*np.sin(j1) + Y_6_0_rot[1]*np.cos(j1))
        num2 = (X_6_0_rot[0]*np.sin(j1)-X_6_0_rot[1]*np.cos(j1))

        if np.round(j5,5) == 0:
            j6 = 0.0
        else:
            j6 = np.arctan2((num1/np.sin(j5)),(num2/np.sin(j5))) + pi
        if np.isnan(j6):
            j6 = 0.0

        # j3
        T_0_1 = np.array([[np.cos(j1), -np.sin(j1), 0, 0],[np.sin(j1), np.cos(j1), 0, 0],[0,0,1,d1],[0,0,0,1]])
        T_1_6 = np.linalg.inv(T_0_1) @ T
        T_4_5 = np.array([[np.cos(j5), -np.sin(j5), 0, 0],[0,0,-1,-d5],[np.sin(j5), np.cos(j5),0,0],[0,0,0,1]])
        T_5_6 = np.array([[np.cos(j6), -np.sin(j6),0,0],[0,0,1,d6],[-np.sin(j6), -np.cos(j6), 0, 0],[0,0,0,1]])
        T_1_4 = T_1_6 @ np.linalg.inv(T_4_5 @ T_5_6)
        P_1_3 = T_1_4 @ np.array([0,-d4,0,1]).T - np.array([0,0,0,1]).T
        P_1_4 = T_1_4[0:3,3]
        P_1_4_a = -P_1_4[0]
        P_1_4_b = -P_1_4[2]
        P_1_4_c = np.sqrt(P_1_4_a**2 + P_1_4_b**2)
        phi3 = np.arccos((P_1_4_c**2 - a2**2 - a3**2)/(-2*a2*a3))
        j3 = np.pi - phi3
        if np.isnan(j3):
            j3 = 0.0
        # check that (P_1_4_c == c) in non-right triangle
        if np.sqrt(a2**2+a3**2-2*a2*a3*np.cos(phi3)) != P_1_4_c:
            #print("Woops")
            pass

        # j2
        phi4 = np.arctan2(-P_1_4_b,-P_1_4_a)
        phi5 = np.arcsin((-a3 * np.sin(-j3))/P_1_4_c)
        j2 = phi4 - phi5

        # j4
        T_1_2 = np.array([[np.cos(j2), -np.sin(j2), 0, 0],[0, 0, -1, 0],[np.sin(j2), np.cos(j2), 0, 0],[0,0,0,1]])
        T_2_3 = np.array([[np.cos(j3), -np.sin(j3), 0, a2],[np.sin(j3), np.cos(j3), 0, 0],[0,0,1,0],[0,0,0,1]])
        T_3_4 = np.linalg.inv(T_1_2 @ T_2_3) @ T_1_4
        X_3_4_rot = T_3_4[0:3,0]
        j4 = np.arctan2(X_3_4_rot[1],X_3_4_rot[0])

        return j1, j2, j3, j4, j5 ,j6

    def compute_xyz_flexcell(self,X,Y,Z=0,rx=0,ry=0,rz=0):
        ## Inversed axis, (x->y,y->x)
        Z_table_level = -0.080
        #Z_table_level = -0.035
        YMIN = 0
        YMAX = 23
        XMIN = 0
        XMAX = 15
        HOLE_DIST = 0.05
        x_min = -0.072
        x_max = 1.128
        #y_max = 0.831#0.431
        y_max = 0.731 + 0.020 # compensation
        y_min = -0.369

        #comp_x = x_max - ((Y+1)*HOLE_DIST) #(Y+1)
        #comp_y = (y_max - ((X)*HOLE_DIST)) # (X+1)
        comp_x = x_max - ((Y+1)*HOLE_DIST) #(Y+1)
        comp_y = (y_max - ((X)*HOLE_DIST)) # (X+1)
        comp_z = Z_table_level + Z * HOLE_DIST
        rx = rx
        ry = ry
        rz = rz

        return comp_x,comp_y,comp_z

    def compute_yz_joint(self,yc,zc):
        theta = np.arctan(zc/yc)
        alpha = np.pi/4 + theta
        zj = np.sqrt(zc**2 + yc**2) * np.cos(np.pi/2 - alpha)
        yj = np.sqrt(zc**2 + yc**2) * np.sin(np.pi/2 - alpha)
        return yj,zj

    def move_initial_position(self,n_steps=70):
        prev_q = self.get_actual_position()
        x = -0.172
        y = -0.269
        z = -0.118
        solution1 = self.compute_ik_num(x,y,z,rounded=False)
        if (solution1 is not np.nan):
            qt = rtb.jtraj(prev_q, solution1, n_steps)
            #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion1.gif")
            time_used = n_steps * 0.05 * 2
            #plt.pause(0.01)
            solution11 = self.compute_ik_num(x,y,z-0.1,rounded=False)
            if (solution11 is not np.nan):
                qt = rtb.jtraj(solution1, solution11, n_steps)
                #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion11.gif")
                time_used = time_used + n_steps * 0.05 * 2
                #plt.pause(0.01)
                return solution11
        else:
            j1,j2,j3,j4,j5,j6 = self.compute_ik_analytic(x,y,z,rounded=False)
            solution1 = np.array([j1,j2,j3,j4,j5,j6])
            if (solution1 is not np.nan):
                qt = rtb.jtraj(prev_q, solution1, n_steps)
                #ur5e.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion1.gif")
                time_used = n_steps * 0.05 * 2
                #plt.pause(0.01)
                j1,j2,j3,j4,j5,j6 = self.compute_ik_analytic(x,y,z-0.1,rounded=False)
                solution11 = np.array([j1,j2,j3,j4,j5,j6])
                if (solution11 is not np.nan):
                    qt = rtb.jtraj(solution1, solution11, n_steps)
                    #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion11.gif")
                    time_used = time_used + n_steps * 0.05 * 2
                    #plt.pause(0.01)
                    return solution11

    def move_p(self,x,y,z,n_steps=70):
        solution1 = self.compute_ik_num(x,y,z,rounded=False)
        j1,j2,j3,j4,j5,j6 = self.compute_ik_analytic(x,y,z)
        solution_analytic = np.array([j1,j2,j3,j4,j5,j6])
        if (solution1 is not np.nan):
            time_used = n_steps * 0.05 * 2
            return np.array([solution1,solution_analytic])
        else:
            time_used = n_steps * 0.05 * 2
            return np.array([solution_analytic])

    def move_p_with_initial_position(self,x,y,z,n_steps=70):
        prev_q = self.get_actual_position()
        solution1 = self.compute_ik_num(x,y,z+0.1,rounded=False)
        if (solution1 is not np.nan):
            qt = rtb.jtraj(prev_q, solution1, n_steps)
            #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion1.gif")
            #plt.pause(0.01)
            time_used = n_steps * 0.05 * 2
            solution11 = self.compute_ik_num(x,y,z,rounded=False)
            if (solution11 is not np.nan):
                qt = rtb.jtraj(solution1, solution11, n_steps)
                #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion11.gif")
                time_used = time_used + n_steps * 0.05 * 2
                #plt.pause(0.01)
                return solution11
            else:
                j1,j2,j3,j4,j5,j6 = self.compute_ik_analytic(x,y,z)
                solution11 = np.array([j1,j2,j3,j4,j5,j6])
                if (solution11 is not np.nan):
                    qt = rtb.jtraj(solution1, solution11, n_steps)
                    #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion11.gif")
                    time_used = time_used + n_steps * 0.05 * 2
                    #plt.pause(0.01)
                    return solution11
        else:
            j1,j2,j3,j4,j5,j6 = self.compute_ik_analytic(x,y,z)
            solution1 = np.array([j1,j2,j3,j4,j5,j6])
            if (solution1 is not np.nan):
                qt = rtb.jtraj(prev_q, solution1, n_steps)
                #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion1.gif")
                time_used = n_steps * 0.05 * 2
                #plt.pause(0.01)
                j1,j2,j3,j4,j5,j6 = self.compute_ik_analytic(x,y,z)
                solution11 = np.array([j1,j2,j3,j4,j5,j6])
                if (solution11 is not np.nan):
                    qt = rtb.jtraj(solution1, solution11, n_steps)
                    #self.plot(qt.q, backend='pyplot',limits=[-0.2, 1.2, -0.7, 1.7, -0.2, 0.8],jointaxes=True,movie="urmotion11.gif")
                    time_used = time_used + n_steps * 0.05 * 2
                    #plt.pause(0.01)
                    return solution11

    def get_actual_position(self):
        return np.array([self.actual_q0,self.actual_q1,self.actual_q2,
                         self.actual_q3,self.actual_q4,self.actual_q5])

    def get_target_position(self):
        return np.array([self.target_q0,self.target_q1,self.target_q2,
                         self.target_q3,self.target_q4,self.target_q5])

    def get_previous_position(self):
        return np.array([self.prev_q0,self.prev_q1,self.prev_q2,
                         self.prev_q3,self.prev_q4,self.prev_q5])

    def set_actual_position(self,array):
        self.actual_q0 = array[0]
        self.actual_q1 = array[1]
        self.actual_q2 = array[2]
        self.actual_q3 = array[3]
        self.actual_q4 = array[4]
        self.actual_q5 = array[5]


    def set_target_position(self,array):
        self.target_q0 = array[0]
        self.target_q1 = array[1]
        self.target_q2 = array[2]
        self.target_q3 = array[3]
        self.target_q4 = array[4]
        self.target_q5 = array[5]

    def set_previous_position(self,array):
        self.prev_q0 = array[0]
        self.prev_q1 = array[1]
        self.prev_q2 = array[2]
        self.prev_q3 = array[3]
        self.prev_q4 = array[4]
        self.prev_q5 = array[5]