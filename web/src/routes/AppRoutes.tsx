import { Routes, Route, Navigate } from 'react-router-dom';
import { Dashboard } from '@/pages/home/Dashboard';
import { Repositories } from '@/pages/repositories/Repositories';
import { CreateRepository } from '@/pages/repositories/CreateRepository';
import { ConfigureRepository } from '@/pages/repositories/ConfigureRepository';
import { RepositoryDetail } from '@/pages/repositories/RepositoryDetail';
import { ArtifactBrowser } from '@/pages/artifacts/ArtifactBrowser';
import { Upload } from '@/pages/upload/Upload';
import { Security } from '@/pages/security/Security';
import { Analytics } from '@/pages/analytics/Analytics';
import { ActivityLog } from '@/pages/activityLog/ActivityLog';
import { ArtifactUpload } from '@/pages/ArtifactUpload';
import { SecurityScan } from '@/pages/SecurityScan';
import { CleanupPolicy } from '@/pages/cleanup/CleanupPolicy';
import { Trending } from '@/pages/trending/Trending';
import { ProfileSettings } from '@/pages/profileSettings/ProfileSettings';
import { RepositorySettings } from '@/pages/repositorySettings/RepositorySettings';
import { TeamManagement } from '@/pages/teamManagement/TeamManagement';
import { SystemSettings } from '@/pages/systemSettings/SystemSettings';
import { NotificationsPage } from '@/pages/notifications/NotificationsPage';


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