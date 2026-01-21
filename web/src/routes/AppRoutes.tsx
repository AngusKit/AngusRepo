import { Routes, Route, Navigate } from 'react-router-dom';
import { Dashboard } from '@/pages/Dashboard';
import { Repositories } from '@/pages/Repositories';
import { CreateRepository } from '@/pages/CreateRepository';
import { ConfigureRepository } from '@/pages/ConfigureRepository';
import { RepositoryDetail } from '@/pages/RepositoryDetail';
import { ArtifactBrowser } from '@/pages/ArtifactBrowser';
import { Upload } from '@/pages/Upload';
import { Security } from '@/pages/Security';
import { Analytics } from '@/pages/Analytics';
import { ActivityLog } from '@/pages/ActivityLog';
import { ArtifactUpload } from '@/pages/ArtifactUpload';
import { SecurityScan } from '@/pages/SecurityScan';
import { CleanupPolicy } from '@/pages/CleanupPolicy';
import { Trending } from '@/pages/Trending';
import { ProfileSettings } from '@/pages/ProfileSettings';
import { RepositorySettings } from '@/pages/RepositorySettings';
import { TeamManagement } from '@/pages/TeamManagement';
import { SystemSettings } from '@/pages/SystemSettings';
import { NotificationsPage } from '@/pages/NotificationsPage';


export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/home" />} />
      <Route path="/home" element={<Dashboard />} />
      <Route path="/repositories" element={<Repositories />} />
      <Route path="/repositories/create" element={<CreateRepository />} />
      <Route path="/repositories/configure/:repositoryId" element={<ConfigureRepository />} />
      <Route path="/repositories/detail/:repositoryId" element={<RepositoryDetail />} />
      <Route path="/artifacts" element={<ArtifactBrowser />} />
      <Route path="/upload" element={<Upload />} />
      <Route path="/security" element={<Security />} />
      <Route path="/analytics" element={<Analytics />} />
      <Route path="/cleanup" element={<CleanupPolicy />} />
      <Route path="/activity-log" element={<ActivityLog />} />
      <Route path="/trending" element={<Trending />} />
      <Route path="/profile-settings" element={<ProfileSettings />} />
      <Route path="/repository-settings" element={<RepositorySettings />} />
      <Route path="/team-management" element={<TeamManagement />} />
      <Route path="/system-settings" element={<SystemSettings />} />
      <Route path="/notifications" element={<NotificationsPage />} />
    </Routes>
  );
};