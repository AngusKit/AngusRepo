import { useState } from 'react';
import { Users, UserPlus, Mail, Shield, Trash2, Edit, Search, Crown, Check, X } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { useLanguage } from '@/components/LanguageProvider';
import { toast } from 'sonner';

export function TeamManagement() {
  const { t } = useLanguage();
  const [searchQuery, setSearchQuery] = useState('');
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteEmail, setInviteEmail] = useState('');
  const [inviteRole, setInviteRole] = useState('developer');
  
  // Pagination states
  const [membersCurrentPage, setMembersCurrentPage] = useState(1);
  const [invitationsCurrentPage, setInvitationsCurrentPage] = useState(1);
  const itemsPerPage = 10;

  // Members data
  const [members, setMembers] = useState([
    {
      id: '1',
      name: 'John Anderson',
      email: 'john.anderson@company.com',
      role: 'owner',
      avatar: 'JA',
      status: 'active',
      joinedDate: '2023-01-15',
      lastActive: '2 hours ago',
      repositories: 18,
    },
    {
      id: '2',
      name: 'Sarah Johnson',
      email: 'sarah.johnson@company.com',
      role: 'admin',
      avatar: 'SJ',
      status: 'active',
      joinedDate: '2023-03-20',
      lastActive: '5 hours ago',
      repositories: 15,
    },
    {
      id: '3',
      name: 'Michael Chen',
      email: 'michael.chen@company.com',
      role: 'developer',
      avatar: 'MC',
      status: 'active',
      joinedDate: '2023-06-10',
      lastActive: '1 day ago',
      repositories: 12,
    },
    {
      id: '4',
      name: 'Emily Rodriguez',
      email: 'emily.rodriguez@company.com',
      role: 'developer',
      avatar: 'ER',
      status: 'active',
      joinedDate: '2023-08-05',
      lastActive: '3 hours ago',
      repositories: 8,
    },
    {
      id: '5',
      name: 'David Kim',
      email: 'david.kim@company.com',
      role: 'viewer',
      avatar: 'DK',
      status: 'inactive',
      joinedDate: '2023-10-12',
      lastActive: '2 weeks ago',
      repositories: 3,
    },
  ]);

  // Pending invitations
  const [invitations, setInvitations] = useState([
    {
      id: '1',
      email: 'alex.wilson@company.com',
      role: 'developer',
      invitedBy: 'John Anderson',
      invitedDate: '2024-01-18',
      expiresIn: '5 days',
      status: 'pending',
    },
    {
      id: '2',
      email: 'lisa.brown@company.com',
      role: 'viewer',
      invitedBy: 'Sarah Johnson',
      invitedDate: '2024-01-17',
      expiresIn: '6 days',
      status: 'pending',
    },
  ]);

  // Roles data
  const roles = [
    {
      id: 'owner',
      name: t('settings.owner'),
      description: t('settings.fullControlOrganization'),
      permissions: [t('settings.allPermissions'), t('settings.manageBilling'), t('settings.deleteOrganization')],
      members: 1,
      color: 'purple',
    },
    {
      id: 'admin',
      name: t('settings.administrator'),
      description: t('settings.canManageUsersRepositories'),
      permissions: [t('settings.manageRepositories'), t('settings.manageUsers'), t('settings.manageSettings')],
      members: 1,
      color: 'blue',
    },
    {
      id: 'developer',
      name: t('settings.developer'),
      description: t('settings.canUploadDownloadArtifacts'),
      permissions: [t('settings.uploadArtifacts'), t('settings.downloadArtifacts'), t('settings.viewRepositories')],
      members: 2,
      color: 'green',
    },
    {
      id: 'viewer',
      name: t('settings.viewer'),
      description: t('settings.readOnlyAccess'),
      permissions: [t('settings.viewRepositories'), t('settings.downloadArtifacts')],
      members: 1,
      color: 'gray',
    },
  ];

  const getRoleBadgeColor = (role: string) => {
    const colors: Record<string, string> = {
      owner: 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300',
      admin: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
      developer: 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300',
      viewer: 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300',
    };
    return colors[role] || colors.viewer;
  };

  const getRoleIcon = (role: string) => {
    if (role === 'owner') return <Crown className="size-3" />;
    if (role === 'admin') return <Shield className="size-3" />;
    return null;
  };

  const handleInviteMember = () => {
    if (!inviteEmail.trim()) {
      toast.error(t('settings.pleaseEnterEmail'));
      return;
    }
    const newInvitation = {
      id: String(invitations.length + 1),
      email: inviteEmail,
      role: inviteRole,
      invitedBy: 'John Anderson',
      invitedDate: new Date().toISOString().split('T')[0],
      expiresIn: '7 days',
      status: 'pending',
    };
    setInvitations([...invitations, newInvitation]);
    setInviteEmail('');
    setShowInviteModal(false);
    toast.success(`${t('settings.invitationSent')} ${inviteEmail}`);
  };

  const handleCancelInvitation = (id: string, email: string) => {
    setInvitations(invitations.filter(i => i.id !== id));
    toast.success(t('settings.invitationCancelled'));
  };

  const handleRemoveMember = (id: string, name: string) => {
    setMembers(members.filter(m => m.id !== id));
    toast.success(`${name} ${t('settings.memberRemoved')}`);
  };

  const handleUpdateRole = (id: string, newRole: string) => {
    setMembers(members.map(m => m.id === id ? { ...m, role: newRole } : m));
    toast.success(t('settings.roleUpdated'));
  };

  const filteredMembers = members.filter(member =>
    member.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    member.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const paginatedMembers = filteredMembers.slice((membersCurrentPage - 1) * itemsPerPage, membersCurrentPage * itemsPerPage);
  const paginatedInvitations = invitations.slice((invitationsCurrentPage - 1) * itemsPerPage, invitationsCurrentPage * itemsPerPage);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl text-gray-900 dark:text-white">{t('settings.team')}</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">{t('settings.manageTeamMembers')}</p>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.totalMembers')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {members.length}
                </p>
              </div>
              <div className="size-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                <Users className="size-6 text-blue-600 dark:text-blue-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.activeMembers')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {members.filter(m => m.status === 'active').length}
                </p>
              </div>
              <div className="size-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                <Check className="size-6 text-green-600 dark:text-green-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.pendingInvites')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {invitations.length}
                </p>
              </div>
              <div className="size-12 bg-orange-100 dark:bg-orange-900/30 rounded-lg flex items-center justify-center">
                <Mail className="size-6 text-orange-600 dark:text-orange-400" />
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600 dark:text-gray-400">{t('settings.totalRoles')}</p>
                <p className="text-2xl font-semibold text-gray-900 dark:text-white mt-1">
                  {roles.length}
                </p>
              </div>
              <div className="size-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                <Shield className="size-6 text-purple-600 dark:text-purple-400" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Invite Modal */}
      {showInviteModal && (
        <Card className="border-2 border-blue-300 dark:border-blue-700 bg-blue-50 dark:bg-blue-900/10">
          <CardHeader>
            <CardTitle>{t('settings.inviteMember')}</CardTitle>
            <CardDescription>{t('settings.sendInvitation')}</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="inviteEmail">{t('settings.emailAddress')}</Label>
              <Input
                id="inviteEmail"
                type="email"
                value={inviteEmail}
                onChange={(e) => setInviteEmail(e.target.value)}
                placeholder={t('settings.emailPlaceholder')}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="inviteRole">{t('settings.role')}</Label>
              <select
                id="inviteRole"
                value={inviteRole}
                onChange={(e) => setInviteRole(e.target.value)}
                className="w-full px-3 py-2 border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
              >
                <option value="developer">{t('settings.developer')}</option>
                <option value="viewer">{t('settings.viewer')}</option>
                <option value="admin">{t('settings.administrator')}</option>
              </select>
            </div>
            <div className="flex gap-2">
              <Button onClick={handleInviteMember}>{t('settings.sendInvitation')}</Button>
              <Button variant="ghost" onClick={() => {
                setShowInviteModal(false);
                setInviteEmail('');
              }}>
                {t('settings.cancel')}
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      <Tabs defaultValue="members" className="space-y-6">
        <TabsList>
          <TabsTrigger value="members">
            <Users className="mr-2 size-4" />
            {t('settings.members')} ({members.length})
          </TabsTrigger>
          <TabsTrigger value="invitations">
            <Mail className="mr-2 size-4" />
            {t('settings.invitations')} ({invitations.length})
          </TabsTrigger>
          <TabsTrigger value="roles">
            <Shield className="mr-2 size-4" />
            {t('settings.roles')}
          </TabsTrigger>
        </TabsList>

        {/* Members Tab */}
        <TabsContent value="members" className="space-y-4">
          {/* Search and Invite Button Row */}
          <div className="flex items-center gap-3">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-gray-400" />
              <Input
                placeholder="Search members..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
            <Button onClick={() => setShowInviteModal(true)}>
              <UserPlus className="mr-2 size-4" />
              {t('settings.inviteMember')}
            </Button>
          </div>

          {/* Members List */}
          <div className="grid grid-cols-1 gap-4">
            {paginatedMembers.map((member) => (
              <Card key={member.id}>
                <CardContent className="pt-6">
                  <div className="flex items-start gap-4">
                    {/* Avatar */}
                    <div className="size-16 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center flex-shrink-0">
                      <span className="text-xl text-white font-semibold">{member.avatar}</span>
                    </div>

                    {/* Member Info */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-4 mb-2">
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1">
                            <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
                              {member.name}
                            </h3>
                            {member.role === 'owner' && (
                              <Crown className="size-4 text-yellow-500" />
                            )}
                            <Badge
                              className={
                                member.status === 'active'
                                  ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-300'
                                  : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-300'
                              }
                            >
                              {member.status}
                            </Badge>
                          </div>
                          <p className="text-sm text-gray-600 dark:text-gray-400">{member.email}</p>
                        </div>
                        {member.role !== 'owner' && (
                          <div className="flex items-center gap-2">
                            <select
                              value={member.role}
                              onChange={(e) => handleUpdateRole(member.id, e.target.value)}
                              className="px-3 py-1.5 text-sm border border-gray-200 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
                            >
                              <option value="admin">Admin</option>
                              <option value="developer">Developer</option>
                              <option value="viewer">Viewer</option>
                            </select>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => handleRemoveMember(member.id, member.name)}
                            >
                              <Trash2 className="size-4 text-red-500" />
                            </Button>
                          </div>
                        )}
                      </div>

                      {/* Stats */}
                      <div className="grid grid-cols-3 gap-4 py-3 border-t border-gray-200 dark:border-gray-700">
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Role</p>
                          <Badge className={getRoleBadgeColor(member.role)}>
                            {getRoleIcon(member.role)}
                            <span className="ml-1 capitalize">{member.role}</span>
                          </Badge>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Repositories</p>
                          <p className="text-sm font-semibold text-gray-900 dark:text-white">
                            {member.repositories}
                          </p>
                        </div>
                        <div>
                          <p className="text-xs text-gray-500 dark:text-gray-400 mb-1">Last Active</p>
                          <p className="text-sm text-gray-900 dark:text-white">
                            {member.lastActive}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400 mt-2">
                        <span>Joined {member.joinedDate}</span>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Members Pagination */}
          {filteredMembers.length > itemsPerPage && (
            <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 pt-4">
              <div className="text-sm text-gray-600 dark:text-gray-400">
                Showing {((membersCurrentPage - 1) * itemsPerPage) + 1} to {Math.min(membersCurrentPage * itemsPerPage, filteredMembers.length)} of {filteredMembers.length} members
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setMembersCurrentPage(prev => Math.max(1, prev - 1))}
                  disabled={membersCurrentPage === 1}
                >
                  Previous
                </Button>
                <div className="flex items-center gap-1">
                  {Array.from({ length: Math.ceil(filteredMembers.length / itemsPerPage) }, (_, i) => i + 1).map(page => (
                    <Button
                      key={page}
                      variant={page === membersCurrentPage ? "default" : "outline"}
                      size="sm"
                      onClick={() => setMembersCurrentPage(page)}
                      className="min-w-[40px]"
                    >
                      {page}
                    </Button>
                  ))}
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setMembersCurrentPage(prev => Math.min(Math.ceil(filteredMembers.length / itemsPerPage), prev + 1))}
                  disabled={membersCurrentPage === Math.ceil(filteredMembers.length / itemsPerPage)}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </TabsContent>

        {/* Invitations Tab */}
        <TabsContent value="invitations" className="space-y-4">
          {invitations.length === 0 ? (
            <Card>
              <CardContent className="py-12 text-center">
                <Mail className="size-12 text-gray-400 mx-auto mb-4" />
                <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
                  No Pending Invitations
                </h3>
                <p className="text-gray-600 dark:text-gray-400 mb-4">
                  Invite team members to collaborate on your repositories
                </p>
                <Button onClick={() => setShowInviteModal(true)}>
                  <UserPlus className="mr-2 size-4" />
                  Invite Member
                </Button>
              </CardContent>
            </Card>
          ) : (
            <>
              <div className="grid grid-cols-1 gap-4">
                {paginatedInvitations.map((invitation) => (
                  <Card key={invitation.id}>
                    <CardContent className="pt-6">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                          <div className="size-12 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center">
                            <Mail className="size-6 text-gray-600 dark:text-gray-400" />
                          </div>
                          <div>
                            <div className="flex items-center gap-2 mb-1">
                              <h4 className="font-medium text-gray-900 dark:text-white">
                                {invitation.email}
                              </h4>
                              <Badge className={getRoleBadgeColor(invitation.role)}>
                                {invitation.role}
                              </Badge>
                            </div>
                            <p className="text-sm text-gray-600 dark:text-gray-400">
                              Invited by {invitation.invitedBy} on {invitation.invitedDate}
                            </p>
                            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                              Expires in {invitation.expiresIn}
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          <Button variant="outline" size="sm">Resend</Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleCancelInvitation(invitation.id, invitation.email)}
                          >
                            <X className="size-4 text-red-500" />
                          </Button>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>

              {/* Invitations Pagination */}
              {invitations.length > itemsPerPage && (
                <div className="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 pt-4">
                  <div className="text-sm text-gray-600 dark:text-gray-400">
                    Showing {((invitationsCurrentPage - 1) * itemsPerPage) + 1} to {Math.min(invitationsCurrentPage * itemsPerPage, invitations.length)} of {invitations.length} invitations
                  </div>
                  <div className="flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setInvitationsCurrentPage(prev => Math.max(1, prev - 1))}
                      disabled={invitationsCurrentPage === 1}
                    >
                      Previous
                    </Button>
                    <div className="flex items-center gap-1">
                      {Array.from({ length: Math.ceil(invitations.length / itemsPerPage) }, (_, i) => i + 1).map(page => (
                        <Button
                          key={page}
                          variant={page === invitationsCurrentPage ? "default" : "outline"}
                          size="sm"
                          onClick={() => setInvitationsCurrentPage(page)}
                          className="min-w-[40px]"
                        >
                          {page}
                        </Button>
                      ))}
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setInvitationsCurrentPage(prev => Math.min(Math.ceil(invitations.length / itemsPerPage), prev + 1))}
                      disabled={invitationsCurrentPage === Math.ceil(invitations.length / itemsPerPage)}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </TabsContent>

        {/* Roles Tab */}
        <TabsContent value="roles" className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {roles.map((role) => (
              <Card key={role.id}>
                <CardHeader>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      {role.id === 'owner' && <Crown className="size-5 text-yellow-500" />}
                      {role.id === 'admin' && <Shield className="size-5 text-blue-500" />}
                      <CardTitle>{role.name}</CardTitle>
                    </div>
                    <Badge className={getRoleBadgeColor(role.id)}>
                      {role.members} {role.members === 1 ? 'member' : 'members'}
                    </Badge>
                  </div>
                  <CardDescription>{role.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    <h4 className="text-sm font-medium text-gray-900 dark:text-white mb-3">
                      Permissions:
                    </h4>
                    {role.permissions.map((permission, idx) => (
                      <div key={idx} className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
                        <Check className="size-4 text-green-500 flex-shrink-0" />
                        <span>{permission}</span>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}